package cn.chenhub.util;

/*
 * Created by chenq on 2017/4/15,015.
 */


import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


public class RSAUtil {

    private PrivateKey getPrivateKey() throws Exception {
        String priKeyData = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAt5yrcHAAjhglnCEn" +
                "6yecMWPeUXcMyo0+itXrLlkpcKIIyqPw546bGThhlb1ppX1ySX/OUA4jSakHekNP" +
                "5eWPawIDAQABAkBbr9pUPTmpuxkcy9m5LYBrkWk02PQEOV/fyE62SEPPP+GRhv4Q" +
                "Fgsu+V2GCwPQ69E3LzKHPsSNpSosIHSO4g3hAiEAywlDfGIl6acnakPrmJE0IL8q" +
                "vuO3FtsHBrpkUuOnXakCIQDngkKfjV8XwZn3Rv0vl20VAHb/IhwZfhejtsK+XwNo" +
                "8wIgGA5n7ZPfdBi3BdM4VeJWb87WrLlkVxPqeDSbcGrCyMkCIQCqdr+XvADI/UTh" +
                "TuQepuErFayJMBSAsNe3NFsw0cUxAQIhAIDGWlcQ+qcLc/yJ/Qb3EeiaonnfWs4m" +
                "7MHx821EWbGc";
        byte[] keyBytes = Base64.getDecoder().decode(priKeyData.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private byte[] hashed(String content) throws NoSuchAlgorithmException {
        byte[] bEncode = content.getBytes();
        MessageDigest digest = MessageDigest.getInstance("MD5");
        return digest.digest(bEncode);
    }

    private byte[] sign(RSAPrivateKey pri, byte[] hashed) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        int hashLen = hashed.length;
        byte[] prefix = new byte[] {48, 32, 48, 12, 6, 8, 42, -122, 72, -122,-9, 13, 2, 5, 5, 0, 4, 16};
        int tLen = prefix.length+hashLen;
        int k = (pri.getModulus().bitLength() + 7) / 8;

        byte[] em = new byte[k];
        em[1] = (byte) 1;
        for (int i = 2; i < k - tLen - 1; i++) {
            em[i] = (byte) 0xff;
        }
        System.arraycopy(prefix,0,em,k-tLen,prefix.length);
        System.arraycopy(hashed, 0, em, k - hashLen, hashLen);
        BigInteger m = new BigInteger(em);
        byte[] src = m.modPow(pri.getPrivateExponent(), pri.getModulus()).toByteArray();
        int numPaddingBytes = em.length - src.length;
        for (int i = 0; i < numPaddingBytes; i++) {
            em[i] = 0;
        }
        if (src.length > em.length) {
            System.arraycopy(src, 1, em, numPaddingBytes + 1, src.length - 1);
        } else {
            System.arraycopy(src, 0, em, numPaddingBytes, src.length);
        }
        return em;
    }


    private String byte2HexStr(byte[] b)
    {
        String stmp;
        StringBuilder sb = new StringBuilder("");
        for (byte aB : b) {
            stmp = Integer.toHexString(aB & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
        }
        return sb.toString().toUpperCase().trim();
    }

    public String rsaSign(String content) throws Exception {
        RSAUtil util = new RSAUtil();
        PrivateKey pk = util.getPrivateKey();
        RSAPrivateKey rpk = (RSAPrivateKey) pk;
        byte[] a = util.hashed(content);
        byte[] result = util.sign(rpk, a);
        String mm = util.byte2HexStr(result).toLowerCase();
        return  "<!-- "+mm+" -->\n"+content;
    }

    public static void main(String[] args) throws Exception {
        String timestamp = "1492182937388";
        String name = "chenq";
        String content = "<ObtainTicketResponse>" +
                "<message></message>" +
                "<prolongationPeriod>607875500</prolongationPeriod>" +
                "<responseCode>OK</responseCode>" +
                "<salt>"+timestamp+"</salt>" +
                "<ticketId>1</ticketId>" +
                "<ticketProperties>licensee="+name+"	licenseType=0	</ticketProperties>" +
                "</ObtainTicketResponse>";
        RSAUtil util = new RSAUtil();
        PrivateKey pk = util.getPrivateKey();
        RSAPrivateKey rpk = (RSAPrivateKey) pk;
        byte[] a = util.hashed(content);
        byte[] result = util.sign(rpk, a);
        String mm = util.byte2HexStr(result).toLowerCase();
        System.out.println("<!-- "+mm+" -->\n"+content);
    }
}