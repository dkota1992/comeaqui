package com.comeaqui.eduardorodriguez.comeaqui.objects.firebase_objects;

import java.io.Serializable;

public class ChatFirebaseObject implements Serializable {
    public String id;
    public String signature;
    public FirebaseUser user1;
    public FirebaseUser user2;
    public String last_message;
}