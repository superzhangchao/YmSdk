package com.ym.game.plugin.google;

import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import androidx.annotation.RequiresApi;

public class Security {


    public static String KEY_FACTORY_ALGORITHM = "RSA";
    public static String SIGNATURE_ALGORITHM = "SHA1withRSA";
    public static String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArEaiCq7os9cmF" +
            "+i564+pIOiSOVZa/LRzu0K79Dg6wKWjnJ1PkHAa4ZOJ81KrxyFk3q3UiJ3lNsTCdW216+KKdKp+YCOFLs" +
            "sN+4FKjFBqY9lJbm6uuxZ9cPugMOTVFrVlmreYyhIY4jysfo4+LeyEmB7D20X7M+7diCRBEIsOY9lA2ne" +
            "OtD6j0BR4rhLGR3xjN0LGrhCCdbw42+eIkc/awbY7FypLMJjbAmEnNBe1tlOxxX6ZgspwAlY8XjnX832l" +
            "xxHdnuJKSPGtYCQLSt/LYc/go90/kc/U+oPtQy/KgCiQEcKeIL1a6AB294JDogkHuqRIeXIu1n4sAfzG" +
            "cshrJQIDAQAB";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean verifyPurchase(String base64PublicKey, String signedData, String signature){
        if ((TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64PublicKey)
                || TextUtils.isEmpty(signature))) {
//            Log.w(TAG, "Purchase verification failed: missing data.")
            return false;
        }
        PublicKey key = generatePublicKey(base64PublicKey);
        return verify(key,signedData,signature);
    }

    private static PublicKey generatePublicKey(String encodedPublicKey){
        try {
            byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
        } catch (NoSuchAlgorithmException e) {
            // "RSA" is guaranteed to be available.
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static boolean verify(PublicKey publicKey, String signedData, String signature){
        byte[] signatureBytes;
        try {
             signatureBytes = Base64.decode(signature, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
//            Log.w(TAG, "Base64 decoding failed.")
            return false;
        }
        try {
            Signature signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM);
            signatureAlgorithm.initVerify(publicKey);
            signatureAlgorithm.update(signedData.getBytes(StandardCharsets.UTF_8));
            if (!signatureAlgorithm.verify(signatureBytes)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // "RSA" is guaranteed to be available.
            e.printStackTrace();
        }
        return false;
    }
}
