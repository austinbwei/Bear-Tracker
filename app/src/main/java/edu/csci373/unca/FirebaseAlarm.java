package edu.csci373.unca;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import static android.content.Context.ALARM_SERVICE;

public class FirebaseAlarm extends BroadcastReceiver {

    private static final String TAG = "FirebaseAlarm";
    private FirebaseFirestore db;
    private CollectionReference mFences;
    private static final double INCREMENT_VALUE = 20;
    private static final double MAX_RADIUS = 420;
    private String documentID;
    private int alarmID;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "Received alarm broadcast");

        documentID = intent.getExtras().getString("documentID");
        alarmID = intent.getExtras().getInt("alarmID");

        Log.d(TAG, "Extras...DocumentID: " + documentID + " AlarmID: " + alarmID);

        db = FirebaseFirestore.getInstance();
        mFences = db.collection("geofences");

        db.runTransaction(new Transaction.Function<Double>() {
            @Override
            public Double apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference dr = mFences.document(documentID);
                DocumentSnapshot snapshot = transaction.get(dr);
                double newRadius = snapshot.getDouble("radius") + INCREMENT_VALUE;

                // Increment radius if below max radius, otherwise delete document
                if (newRadius <= MAX_RADIUS) {
                    transaction.update(dr, "radius", newRadius);
                    Log.d(TAG, "Incremented " + documentID + " Radius now: " + newRadius);
                    return newRadius;
                } else {
                    transaction.delete(dr);

                    Log.d(TAG, "Document deleted");

                    Intent intent = new Intent(context, FirebaseAlarm.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), alarmID, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
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

}
