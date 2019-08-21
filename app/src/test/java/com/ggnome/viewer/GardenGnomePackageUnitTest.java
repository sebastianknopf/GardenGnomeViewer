package com.ggnome.viewer;

import com.ggnome.viewer.helper.GardenGnomePackage;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * TestCase for GardenGnomePackage class.
 */
public class GardenGnomePackageUnitTest {

    @Test
    public void singlePano() throws Exception {
        String inputFileName = this.getClass().getResource("single_pano.ggpkg").getPath();

        File testFile = new File(inputFileName);
        assertEquals(true, testFile.exists());

        GardenGnomePackage singlePackage = new GardenGnomePackage(testFile.getAbsolutePath());
        singlePackage.open("D:\\SinglePanoCase\\");
        singlePackage.close();

        assertNotNull("preview image is null", singlePackage.getPreviewImageData());
        assertNotNull("meta data is null", singlePackage.getMetaData());
    }

    @Test
    public void tourPano() throws Exception {
        String inputFileName = this.getClass().getResource("tour_pano.ggpkg").getPath();

        File testFile = new File(inputFileName);
        assertEquals(true, testFile.exists());

        GardenGnomePackage tourPackage = new GardenGnomePackage(testFile.getAbsolutePath());
        tourPackage.open("D:\\TourPanoCase\\");
        tourPackage.close();

        assertNotNull("preview image is null", tourPackage.getPreviewImageData());
        assertNotNull("meta data is null", tourPackage.getMetaData());
    }

}
