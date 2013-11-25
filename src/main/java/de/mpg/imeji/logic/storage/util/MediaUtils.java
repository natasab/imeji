/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.storage.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.Info;

import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Mehtods to help wotk with images
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class MediaUtils
{
    private static Logger logger = Logger.getLogger(MediaUtils.class);

    /**
     * Return true if imagemagick is installed on the current system<br/>
     * TODO Ye: Execute when upload page shows and show install ImageMagick tips
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static boolean verifyImageMagickInstallation() throws IOException, URISyntaxException
    {
        String imPath = getImageMagickInstallationPath();
        ConvertCmd cmd = new ConvertCmd(false);
        cmd.setSearchPath(imPath);
        IMOperation op = new IMOperation();
        // get ImageMagick version
        op.version();
        try
        {
            cmd.run(op);
        }
        catch (Exception e)
        {
            logger.error("imagemagick not installed", e);
            return false;
        }
        return true;
    }

    /**
     * User imagemagick to convert any image into a jpeg
     * 
     * @param bytes
     * @param extension
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    public static byte[] convertToJPEG(File tmp, String extension) throws IOException, URISyntaxException,
            InterruptedException, IM4JavaException
    {
        // In case the file is made of many frames, (for instance videos), generate only the frames from 0 to 48 to
        // avoid high memory consumption
        String path = tmp.getAbsolutePath() + "[0-48]";
        ConvertCmd cmd = getConvert();
        // create the operation, add images and operators/options
        IMOperation op = new IMOperation();
        op.colorspace(findColorSpace(tmp));
        op.strip();
        op.addImage(path);
        // op.colorspace("RGB");
        File jpeg = File.createTempFile("uploadMagick", ".jpg");
        try
        {
            op.addImage(jpeg.getAbsolutePath());
            cmd.run(op);
            int frame = getNonBlankFrame(jpeg.getAbsolutePath());
            if (frame >= 0)
            {
                File f = new File(FilenameUtils.getFullPath(jpeg.getAbsolutePath())
                        + FilenameUtils.getBaseName(jpeg.getAbsolutePath()) + "-" + frame + ".jpg");
                return FileUtils.readFileToByteArray(f);
            }
            return FileUtils.readFileToByteArray(jpeg);
        }
        finally
        {
            removeFilesCreatedByImageMagick(jpeg.getAbsolutePath());
            FileUtils.deleteQuietly(jpeg);
        }
    }

    public static byte[] resize()
    {
        return null;
    }

    /**
     * Find the colorspace of the file
     * 
     * @param tmp
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws IM4JavaException
     * @throws URISyntaxException
     */
    public static String findColorSpace(File tmp)
    {
        try
        {
            Info imageInfo = new Info(tmp.getAbsolutePath());
            String cs = imageInfo.getProperty("Colorspace");
            if (cs != null)
                return cs;
        }
        catch (Exception e)
        {
            logger.error("No color space found!", e);
        }
        return "RGB";
    }

    /**
     * Search for the first non blank image generated by imagemagick, based on commandline: convert image.jpg -shave
     * 1%x1% -resize 40% -fuzz 10% -trim +repage info: | grep ' 1x1 '
     * 
     * @param path
     * @return
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IM4JavaException
     */
    private static int getNonBlankFrame(String path) throws IOException, URISyntaxException, InterruptedException,
            IM4JavaException
    {
        ConvertCmd cmd = getConvert();
        int count = 0;
        String dir = FilenameUtils.getFullPath(path);
        String pathBase = FilenameUtils.getBaseName(path);
        File f = new File(dir + pathBase + "-" + count + ".jpg");
        while (f.exists())
        {
            IMOperation op = new IMOperation();
            op.addImage();
            op.shave(1, 1, true);
            op.fuzz(10.0, true);
            op.trim();
            op.addImage();
            File trim = File.createTempFile("trim", ".jpg");
            try
            {
                cmd.run(op, f.getAbsolutePath(), trim.getAbsolutePath());
                Info info = new Info(trim.getAbsolutePath());
                if (!info.getImageGeometry().contains("1x1"))
                    return count;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                String newPath = f.getAbsolutePath().replace("-" + count, "-" + Integer.valueOf(count + 1));
                f = new File(newPath);
                count++;
                trim.delete();
            }
        }
        return -1;
    }

    /**
     * Remove the files created by imagemagick.
     * 
     * @param path
     */
    private static void removeFilesCreatedByImageMagick(String path)
    {
        int count = 0;
        String dir = FilenameUtils.getFullPath(path);
        String pathBase = FilenameUtils.getBaseName(path);
        File f = new File(dir + pathBase + "-" + count + ".jpg");
        while (f.exists())
        {
            String newPath = f.getAbsolutePath().replace("-" + count, "-" + Integer.valueOf(count + 1));
            f.delete();
            f = new File(newPath);
            count++;
        }
    }

    /**
     * Create a {@link ConvertCmd}
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static ConvertCmd getConvert() throws IOException, URISyntaxException
    {
        String magickPath = getImageMagickInstallationPath();
        // TODO Ye:ConvertCmd(true) to use GraphicsMagick, which is said faster
        ConvertCmd cmd = new ConvertCmd(false);
        cmd.setSearchPath(magickPath);
        return cmd;
    }

    /**
     * Return property imeji.imagemagick.installpath
     * 
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private static String getImageMagickInstallationPath() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("imeji.imagemagick.installpath");
    }
}