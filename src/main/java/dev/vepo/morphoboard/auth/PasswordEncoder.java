package dev.vepo.morphoboard.auth;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import dev.vepo.morphoboard.infra.MorphoBoardException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PasswordEncoder {

    @Inject
    @ConfigProperty(name = "password.iterations")
    int passwordIterations;

    @Inject
    @ConfigProperty(name = "password.key.length")
    int passwordKeyLength;

    @Inject
    @ConfigProperty(name = "password.algorithm")
    String algorithm;

    @Inject
    @ConfigProperty(name = "password.salt")
    String salt;

    public String hashPassword(String password) {
        char[] chars = password.toCharArray();
        byte[] bytes = salt.getBytes();
        PBEKeySpec spec = new PBEKeySpec(chars, bytes, passwordIterations, passwordKeyLength);
        Arrays.fill(chars, Character.MIN_VALUE);

        try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance(algorithm);
            byte[] securePassword = fac.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(securePassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new MorphoBoardException("Error encoding password", ex);
        } finally {
            spec.clearPassword();
        }
    }

    public boolean matches(String plainPassword, String hashedPassword) {
        Objects.requireNonNull(hashedPassword, "hashedPassword cannot be null!");
        Objects.requireNonNull(plainPassword, "plainPassword cannot be null!");
        return hashPassword(plainPassword).compareTo(hashedPassword) == 0;
    }
}