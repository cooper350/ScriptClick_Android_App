package com.example.mainscreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemClock;
import android.util.Log;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public class RecordLines extends Fragment {

    private TextView timerText;
    private long startTime = 0L;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String audioFilePath;
    private ImageButton recordButton, playButton, deleteButton;
    private boolean isRecording = false;
    private String troupeName = "Recording Booth";
    private StorageReference audioStorageReference;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionsGranted = false;
    private boolean audioExistsOnFirebase = false;
    private TextView largeText;
    private ObjectAnimator colorAnimator;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_lines, container, false);

        timerText = view.findViewById(R.id.timer_text);
        recordButton = view.findViewById(R.id.record_button);
        playButton = view.findViewById(R.id.play_button);
        deleteButton = view.findViewById(R.id.delete_button);
        largeText = view.findViewById(R.id.large_text);
        //loadRomeoLinesFromDatabase();

        audioStorageReference = FirebaseStorage.getInstance().getReference().child("Romeo.mp3");
        checkAudioExistenceOnFirebase();

        audioFilePath = getActivity().getExternalCacheDir().getAbsolutePath();
        audioFilePath += "/Romeo.mp3";

        timerHandler = new Handler();

        // Retrieve the selected troupe name from the arguments
        // Set the toolbar title to the troupe name
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("selectedItem")) {
            String clickedTroupe = arguments.getString("selectedItem");
            String[] troupeInfo = clickedTroupe.split(":");
            troupeName = troupeInfo[0].trim();
        }

        // Set the toolbar title to the troupe name
        TextView titleTroupeTextView = view.findViewById(R.id.titleTroupe);
        titleTroupeTextView.setText(troupeName);

        audioStorageReference = FirebaseStorage.getInstance().getReference().child("Romeo.mp3");

        requestPermissions();

        ImageView back_arrow = view.findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });

        recordButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Record button clicked");
            if (!permissionsGranted) {
                Log.d("MainActivity", "Requesting permissions");
                requestPermissions();
            } else {
                if (!isRecording) {
                    Log.d("MainActivity", "Starting recording");
                    startRecording();
                    Toast.makeText(requireContext(), "Recording started", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("MainActivity", "Stopping recording");
                    stopRecording();
                    Toast.makeText(requireContext(), "Recording stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView imageTS = view.findViewById(R.id.imageTS);
        imageTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View script_page) {
                Intent intent2 = new Intent(getActivity(), troupe_settings.class);
                startActivity(intent2);
            }
        });

        playButton.setOnClickListener(v -> playAudio());

        deleteButton.setOnClickListener(v -> deleteAudio());

        updateButtonStates();

        return view;
    }

    private void checkAudioExistenceOnFirebase() {
        audioStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                audioExistsOnFirebase = true;
                updateButtonStates();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                audioExistsOnFirebase = false;
                updateButtonStates();
            }
        });
    }

    private void updateButtonStates() {
        playButton.setEnabled(audioExistsOnFirebase);
        deleteButton.setEnabled(audioExistsOnFirebase);
    }

    private void requestPermissions() {
        Log.d("MainActivity", "Requesting permissions");
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    private void startRecording() {
        if (audioExistsOnFirebase) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Overwrite existing recording")
                    .setMessage("Are you sure you want to overwrite the existing recording?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        initiateRecording();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                    .show();
        } else {
            initiateRecording();
        }
    }

    private void initiateRecording() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            audioFilePath = getActivity().getExternalCacheDir().getAbsolutePath();
            audioFilePath += "/Romeo.mp3";
            mediaRecorder.setOutputFile(audioFilePath);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mediaRecorder.start();
        isRecording = true;
        startTime = SystemClock.uptimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        startColorAnimation(true);
    }


    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;

            uploadAudioToFirebaseStorage();
        }

        isRecording = false;
        timerHandler.removeCallbacks(timerRunnable);
        startColorAnimation(false);
    }

    private void uploadAudioToFirebaseStorage() {
        Uri audioFileUri = Uri.fromFile(new File(audioFilePath));

        UploadTask uploadTask = audioStorageReference.putFile(audioFileUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return audioStorageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    // Show a message to the user that the audio file was successfully uploaded to Firebase Storage
                    Toast.makeText(requireContext(), "Audio uploaded successfully to Firebase Storage", Toast.LENGTH_SHORT).show();
                } else {
                    // Show a message to the user that the audio file upload failed
                    Toast.makeText(requireContext(), "Failed to upload audio to Firebase Storage", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void playAudio() {
        if (audioExistsOnFirebase) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();

                // Retrieve the download URL of the audio from Firebase Storage
                audioStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        try {
                            mediaPlayer.setDataSource(requireContext(), uri);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } else {
                    mediaPlayer = null;
                    playAudio();
                }
            }
        } else {
            // Show a message to the user that there is no audio to play
            Toast.makeText(requireContext(), "No audio to play", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAudio() {
        if (audioExistsOnFirebase) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Delete existing recording")
                    .setMessage("Are you sure you want to delete the existing recording?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteAudioFromFirebaseStorage();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                    .show();
        } else {
            Toast.makeText(requireContext(), "No audio to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAudioFromFirebaseStorage() {
        audioStorageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Audio deleted successfully from Firebase Storage
                        Toast.makeText(requireContext(), "Audio deleted successfully from Firebase Storage", Toast.LENGTH_SHORT).show();
                        audioExistsOnFirebase = false;
                        updateButtonStates();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Audio deletion failed
                        Toast.makeText(requireContext(), "Failed to delete audio from Firebase Storage", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds %= 60;
            timerText.setText(String.format("%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 0);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    // Permission not granted, show a message to the user
                    Toast.makeText(requireContext(), "Permissions not granted. Please allow permissions to use this feature.", Toast.LENGTH_LONG).show();
                } else {
                    // User has checked "Don't ask again", guide them to app settings
                    Toast.makeText(requireContext(), "Permissions not granted. Please enable permissions in settings to use this feature.", Toast.LENGTH_LONG).show();
                    openAppSettings();
                }
                permissionsGranted = false;
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void startColorAnimation(boolean start) {
        if (start) {
            final String[] words = largeText.getText().toString().split(" ");
            final SpannableStringBuilder spannable = new SpannableStringBuilder(largeText.getText());
            final Handler handler = new Handler(Looper.getMainLooper());
            final int animationDurationPerWord = 250; // Set the duration for each word in milliseconds

            Runnable runnable = new Runnable() {
                int wordIndex = 0;
                int lastIndex = 0;

                @Override
                public void run() {
                    if (wordIndex < words.length) {
                        int start = largeText.getText().toString().indexOf(words[wordIndex], lastIndex);
                        int end = start + words[wordIndex].length();
                        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.scriptclick_blue)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        largeText.setText(spannable);
                        lastIndex = end;
                        wordIndex++;
                        handler.postDelayed(this, animationDurationPerWord);
                    } else {
                        handler.removeCallbacks(this);
                    }
                }
            };

            handler.postDelayed(runnable, animationDurationPerWord);
        } else {
            if (largeText != null) {
                largeText.setTextColor(Color.BLACK);
            }
        }
    }

    private void loadRomeoLinesFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("troupes");
        databaseReference.orderByKey().equalTo("troupe1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The troupe exists in the database
                    Log.d("TroupeData", "Troupe found in the database");

                    StringBuilder romeoLinesBuilder = new StringBuilder();
                    for (DataSnapshot troupeSnapshot : dataSnapshot.getChildren()) {
                        String script = troupeSnapshot.child("script").getValue(String.class);

                        // Find and extract Romeo's lines from the script
                        String[] lines = script.split("\n");
                        boolean romeoSpeaking = false;
                        for (String line : lines) {
                            if (line.startsWith("Romeo: ")) {
                                romeoLinesBuilder.append(line.substring(7).trim()).append("\n");
                                romeoSpeaking = true;
                            } else if (line.contains(":")) {
                                if (romeoSpeaking) {
                                    romeoSpeaking = false;
                                }
                            } else {
                                if (romeoSpeaking) {
                                    romeoLinesBuilder.append(line.trim()).append("\n");
                                }
                            }
                        }
                    }
                    String romeoLines = romeoLinesBuilder.toString().trim();

                    // Set Romeo's lines to the text view
                    TextView scriptTextView = getView().findViewById(R.id.large_text);
                    scriptTextView.setText(romeoLines);
                    Log.d("TroupeData", "Romeo's lines successfully displayed");
                    Log.d("TroupeData", "Romeo's lines: " + romeoLines);
                } else {
                    // The troupe does not exist in the database
                    Log.e("TroupeData", "Troupe not found in the database");
                    System.out.println("Troupe not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.e("TroupeData", "Error fetching data: " + databaseError.getMessage());
            }
        });

    }



}