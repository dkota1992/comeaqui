package com.comeaqui.eduardorodriguez.comeaqui.objects.firebase_objects;

import java.io.Serializable;

public class MessageFirebaseObject implements Serializable {
    public String message;
    public String chat;
    public FirebaseUser sender;
}
