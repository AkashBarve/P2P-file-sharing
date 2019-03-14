package com;

public class Constants {
    public static final String root = System.getProperty("user.dir");
    static final String common = root + "/com/Common.cfg";
    static final String peers = root + "/com/PeerInfo.cfg";

    public static final String HANDSHAKEHEADER = "P2PFILESHARINGPROJ";
    public static final String ZERO_BITS = "0000000000";

    private static Integer NumberOfPreferredNeighbors;
    private static Integer UnchokingInterval;
    private static Integer OptimisticUnchokingInterval;
    private static String FileName;
    private static Integer FileSize;
    private static Integer PieceSize;


    private static Integer throw_()
    {
        throw new RuntimeException("Variable already set");
    }

    private static String throw_S()
    {
        throw new RuntimeException("Variable already set");
    }

    public static int getNumberOfPreferredNeighbors()
    {
        return NumberOfPreferredNeighbors;
    }

    public static void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors)
    {
        NumberOfPreferredNeighbors = NumberOfPreferredNeighbors == null ? numberOfPreferredNeighbors : throw_();
    }

    public static int getUnchokingInterval()
    {
        return UnchokingInterval;
    }

    public static void setUnchokingInterval(int unchokingInterval)
    {
        UnchokingInterval = UnchokingInterval == null ? unchokingInterval : throw_();
    }

    public static int getOptimisticUnchokingInterval()
    {
        return OptimisticUnchokingInterval;
    }

    public static void setOptimisticUnchokingInterval(int optimisticUnchokingInterval)
    {
        OptimisticUnchokingInterval = OptimisticUnchokingInterval == null ? optimisticUnchokingInterval : throw_();
    }

    public static String getFileName()
    {
        return FileName;
    }

    public static void setFileName(String fileName)
    {
        FileName = FileName == null ? fileName : throw_S();
    }

    public static int getFileSize()
    {
        return FileSize;
    }

    public static void setFileSize(int fileSize)
    {
        FileSize = FileSize == null ? fileSize : throw_();
    }

    public static int getPieceSize()
    {
        return PieceSize;
    }

    public static void setPieceSize(int pieceSize)
    {
        PieceSize = PieceSize == null ? pieceSize : throw_();
    }
}
