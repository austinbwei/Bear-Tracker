package edu.csci373.unca;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class FirebaseJobService extends JobService {

    private static final String TAG = "FirebaseJobService";
    private boolean jobCancelled = false;
    private FirebaseFirestore db;
    private CollectionReference mFences;
    private static final double INCREMENT_VALUE = 20;
    private static final double MAX_RADIUS = 500;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseFirestore.getInstance();
        mFences = db.collection("geofences");
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");

        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                transaction("Sya7hPsJCw8ppox3uwS5");


                if (jobCancelled) {
                    return;
                }
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }

    private void transaction(final String documentID) {
        db.runTransaction(new Transaction.Function<Double>() {
            @Override
            public Double apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference dr = mFences.document(documentID);
                DocumentSnapshot snapshot = transaction.get(dr);
                double newRadius = snapshot.getDouble("radius") + INCREMENT_VALUE;

                // Increase radius if below max radius, otherwise delete document
                if (newRadius <= MAX_RADIUS) {
                    transaction.update(dr, "radius", newRadius);
                    return newRadius;
                } else {
                    transaction.delete(dr);
                    return null;
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Double>() {
            @Override
            public void onSuccess(Double result) {
                Log.d(TAG, "Transaction success: " + result);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Transaction failure: " + e);
            }
        });
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled");

        jobCancelled = true;
        return true;
    }
}
