package com.google.bitcoin.core.signers;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.crypto.DeterministicKey;

import java.util.List;
import java.util.Map;

/**
 * @author devrandom
 */
public interface RegisteringTransactionSigner extends TransactionSigner {
    /**
     * Register with signing service, if required
     *
     * @param accountKeys public keys for wallet, recovery.  All derivation levels under
     *                    this should be non-hardened.
     * @param pii personal information if any, to be provided to service during registration
     * @param parameters any parameters that customize the way the service will work with this wallet
     */
    void register(List<DeterministicKey> accountKeys,
                  Object pii,
                  Object parameters);

    /**
     * Verify personal information items, such as phone, email and OTP by supplying codes entered
     * by the user.
     *
     * @param verifications the code for each pii item to be verified
     */
    void completeRegistration(Map<String, String> verifications);

    /** Connect to existing registration */
    void connect(DeterministicKey accountKey);

    /** Keys provided by the service, to marry with the user keys.  Can be called after registration is complete. */
    List<DeterministicKey> getServiceKeys();

    /** Get next fee destination address */
    Address getFeeAddress();

    /** Get the amount of fee required
     *
     * @param amount the amount that will be sent from the wallet
     * @return the fee required for the amount
     */
    long getFeeForAmount(long amount);

    public interface RegistrationListener {
        /**
         * A phone was provided during registration, and the type was detected by the service.
         *
         * @param phoneId "default" for now
         * @param landline whether phone is a landline
         * @param phoneVerificationCode a code to display to the user that they can enter when called.
         *                              null if !landline
         */
        void onPhoneTypeDetected(String phoneId, boolean landline, String phoneVerificationCode);
        /** The registration is complete, and {@link com.google.bitcoin.core.signers.TransactionSigner#getServiceKeys()} may now be called */
        void onRegistrationComplete();
    }
}
