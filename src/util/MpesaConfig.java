package util;

public class MpesaConfig {

    // ── Sandbox Credentials ───────────────────────────────────────────────────
    public static final String CONSUMER_KEY    = "oYxC9pYxS8cLqLBWUKYSdXusDte0JqHYJnjHzWDAm5mzbxDi";
    public static final String CONSUMER_SECRET = "6ltd5OUAiFqOGhjDUQHCDECMbsippO9vBo5NtZw5T3lDgPLeJOL7gwGLpsZ2Bp5S";
    public static final String PASSKEY         = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    public static final String SHORTCODE       = "174379";

    // ── Sandbox API URLs ──────────────────────────────────────────────────────
    public static final String AUTH_URL     = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    public static final String STK_PUSH_URL = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";

    // ── Callback URL ──────────────────────────────────────────────────────────
    // This is your ngrok URL — update this every time you start ngrok
    public static final String CALLBACK_URL = "https://unrestricted-lacey-nonconstricted.ngrok-free.dev/mpesa/callback";

    // ── Payment Details ───────────────────────────────────────────────────────
    public static final double  HOSTEL_FEE       = 10.00;
    public static final String  ACCOUNT_REFERENCE = "HostelFee";
    public static final String  TRANSACTION_DESC  = "Hostel Fee Payment";
}
