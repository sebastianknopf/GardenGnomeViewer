package com.ggnome.viewer.helper;


import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class GardenGnomePackage implements Closeable {

    // keys for unified access of meta data
    public static String META_CONTENT_TYPE = "META_CONTENT_TYPE";
    public static String META_CONFIG_NAME = "META_CONFIG_NAME";
    public static String META_PREVIEW_NAME = "META_PREVIEW_NAME";

    public static String META_OBJECT_TITLE = "META_OBJECT_TITLE";
    public static String META_OBJECT_DESCRIPTION = "META_OBJECT_DESCRIPTION";
    public static String META_OBJECT_DATETIME = "META_OBJECT_DATETIME";
    public static String META_OBJECT_TAGS = "META_OBJECT_TAGS";
    public static String META_OBJECT_INFO = "META_OBJECT_INFO";
    public static String META_OBJECT_LATITUDE = "META_OBJECT_LATITUDE";
    public static String META_OBJECT_LONGITUDE = "META_OBJECT_LONGITUDE";
    public static String META_OBJECT_COMMENT = "META_OBJECT_COMMENT";
    public static String META_OBJECT_CUSTOM_NODE_ID = "META_OBJECT_CUSTOM_NODE_ID";
    public static String META_OBJECT_SOURCE = "META_OBJECT_SOURCE";
    public static String META_OBJECT_AUTHOR= "META_OBJECT_AUTHOR";
    public static String META_OBJECT_COPYRIGHT = "META_OBJECT_COPYRIGHT";

    private String packageFileName;
    private String packageDirectory;

    // status variables
    private Map<String, byte[]> unpackedSingleFiles;
    private boolean isOpen;

    // required meta data
    private String metaContentType, metaPreviewName, metaConfigName;

    /**
     * Constructs a common helper for a GardenGnomePackage-File.
     *
     * @param packageFileName The absolute file name of the package file.
     */
    public GardenGnomePackage(String packageFileName) {
        this.packageFileName = packageFileName;
        this.unpackedSingleFiles = new HashMap<>();
        this.isOpen = false;

        File packageFile = new File(this.packageFileName);
        if(!packageFile.exists()) {
            throw new RuntimeException("unable to read ggpkg");
        }

        try {
            JSONObject ggInfo = new JSONObject(new String(this.unpackSingleFile("gginfo.json")));

            this.metaContentType = ggInfo.getString("type");
            this.metaConfigName = ggInfo.getString("configuration");
            this.metaPreviewName = ggInfo.getJSONObject("preview").getString("img");
        } catch (Exception e) {
            throw new RuntimeException("unable to read gginfo");
        }
    }

    /**
     * Cleans up the reserved package directory and other resources.
     */
    @Override
    public void close() {
        if(this.isOpen) {
            File cleanDirectory = new File(this.packageDirectory, "ggpkg");
            this.cleanUpDirectory(cleanDirectory);

            this.packageDirectory = null;
            this.isOpen = false;
        }
    }

    /**
     * Unpacks the package file to a specified target path.
     *
     * @param packageDirectory The directory to unpack the archive file.
     * @throws IOException When the package file could not be read.
     */
    public void open(String packageDirectory) throws IOException {
        this.packageDirectory = packageDirectory;

        File targetDirectory = new File(this.packageDirectory, "ggpkg");
        if(!targetDirectory.exists()) {
            targetDirectory.mkdirs();
        }

        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.packageFileName)));
        ZipEntry zipEntry;

        byte[] buffer = new byte[1024];
        int count;

        while((zipEntry = zipInputStream.getNextEntry()) != null) {
            String filename = zipEntry.getName();

            if(zipEntry.isDirectory()) {
                File directory = new File(targetDirectory, filename);
                directory.mkdirs();
                continue;
            }

            File outputFile = new File(targetDirectory, filename);
            if(!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            while((count = zipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }

            outputStream.close();
            zipInputStream.closeEntry();
        }

        this.isOpen = true;
    }

    /**
     * Get package preview image as Bitmap.
     *
     * @return Bitmap The package preview image.
     * @throws IOException When the package file could not be read.
     */
    public byte[] getPreviewImageData() throws IOException {
        return this.unpackSingleFile(this.metaPreviewName);
    }

    /**
     * Get all meta-data about the package file. The meta-data keys can be accessed via
     * constants defined in GardenGnomePackage class.
     *
     * @return Map<String, String>|null Map containing all key-value pairs of meta-data.
     * @throws IOException When the package file could not be read.
     */
    public Map<String, String> getMetaData() throws IOException {
        byte[] configXml = this.unpackSingleFile(this.metaConfigName);
        String configXmlString = new String(configXml);

        try {
            Map<String, String> metaDataMap = new HashMap<>();
            metaDataMap.put(META_CONTENT_TYPE, this.metaContentType);
            metaDataMap.put(META_CONFIG_NAME, this.metaConfigName);
            metaDataMap.put(META_PREVIEW_NAME, this.metaPreviewName);

            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(true);

            XmlPullParser xpp = xmlPullParserFactory.newPullParser();
            xpp.setInput(new StringReader(configXmlString));

            int eventType = xpp.getEventType();
            String tourStartNode = null, nodeId = null;
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equalsIgnoreCase("tour")) {
                        tourStartNode = xpp.getAttributeValue(null, "start");
                    } else if(xpp.getName().equalsIgnoreCase("panorama")) {
                        if(tourStartNode != null && xpp.getAttributeValue(null, "id").equalsIgnoreCase(tourStartNode)) {
                            nodeId = null;
                        } else {
                            nodeId = xpp.getAttributeValue(null, "id");
                        }

                        nodeId = xpp.getAttributeValue(null, "id");
                    } else if(nodeId != null && xpp.getName().equalsIgnoreCase("userdata")) {
                        metaDataMap.put(META_OBJECT_TITLE, xpp.getAttributeValue(null, "title"));
                        metaDataMap.put(META_OBJECT_DESCRIPTION, xpp.getAttributeValue(null, "description"));
                        metaDataMap.put(META_OBJECT_DATETIME, xpp.getAttributeValue(null, "datetime"));
                        metaDataMap.put(META_OBJECT_TAGS, xpp.getAttributeValue(null, "tags"));
                        metaDataMap.put(META_OBJECT_INFO, xpp.getAttributeValue(null, "info"));
                        metaDataMap.put(META_OBJECT_LATITUDE, xpp.getAttributeValue(null, "latitude"));
                        metaDataMap.put(META_OBJECT_LONGITUDE, xpp.getAttributeValue(null, "longitude"));
                        metaDataMap.put(META_OBJECT_COMMENT, xpp.getAttributeValue(null, "comment"));
                        metaDataMap.put(META_OBJECT_CUSTOM_NODE_ID, xpp.getAttributeValue(null, "customnodeid"));
                        metaDataMap.put(META_OBJECT_SOURCE, xpp.getAttributeValue(null, "source"));
                        metaDataMap.put(META_OBJECT_AUTHOR, xpp.getAttributeValue(null, "author"));
                        metaDataMap.put(META_OBJECT_COPYRIGHT, xpp.getAttributeValue(null, "copyright"));
                    }
                }

                eventType = xpp.next();
            }

            return metaDataMap;
        } catch(XmlPullParserException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Unpack a single file and return it's content as byte array. If the file could not be found
     * within the current package, null is returned.
     *
     * @param fileName The file to be unpacked.
     * @return byte[]|null The content of the unpacked file.
     * @throws IOException When the package file could not be read.
     */
    private byte[] unpackSingleFile(String fileName) throws IOException {
        if(this.unpackedSingleFiles.containsKey(fileName)) {
            return this.unpackedSingleFiles.get(fileName);
        }

        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.packageFileName)));
        ZipEntry zipEntry;

        byte[] buffer = new byte[1024];
        int count;

        while((zipEntry = zipInputStream.getNextEntry()) != null) {
            if(zipEntry.getName().equals(fileName)) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                while((count = zipInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, count);
                }

                this.unpackedSingleFiles.put(fileName, outputStream.toByteArray());
                outputStream.close();

                zipInputStream.closeEntry();
                return this.unpackedSingleFiles.get(fileName);
            }

            zipInputStream.closeEntry();
        }

        return null;
    }

    /**
     * Cleans up and deletes the specified directory.
     *
     * @param startDirectory The directory to handle.
     */
    private void cleanUpDirectory(File startDirectory) {
        for(File object : startDirectory.listFiles()) {
            if(object.isDirectory()) {
                this.cleanUpDirectory(object);
            } else {
                object.delete();
            }
        }

        startDirectory.delete();
    }
}
