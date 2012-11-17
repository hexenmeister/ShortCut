package de.as.eclipse.shortcut.ui.views;

import java.text.DateFormat;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import de.as.eclipse.shortcut.Activator;
import de.as.eclipse.shortcut.business.Shortcut;
import de.as.eclipse.shortcut.ui.UIConstants;
import de.as.eclipse.shortcut.ui.UIUtils;

public class ShortcutsLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
    private static ImageRegistry imageRegistry = new ImageRegistry();

    // private static ColorRegistry colorRegistry;

    // XXX: NB: http://www.eclipse.org/articles/Article-SWT-images/graphics-resources.html#Manipulating%20Image%20Data

    @Override
    public String getText(Object element) {
        Shortcut file = (Shortcut) element;
        return file.getName();
    }

    private static int TTCNT = 0;

    public static int COL_INDEX_COLOR_ICON = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_NAME = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_GROUP = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_PRIORITY = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_CATEGORY1 = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_CATEGORY2 = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_LOCATION = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_WORKING_DIR = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_SIZE = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_LAST_MODIFIED = ShortcutsLabelProvider.TTCNT++;

    public static int COL_INDEX_MORECOMMANDS = ShortcutsLabelProvider.TTCNT++;

    @Override
    public String getColumnText(Object element, int columnIndex) {
        String columnText = "";
        Shortcut file = (Shortcut) element;
        if (columnIndex == ShortcutsLabelProvider.COL_INDEX_NAME) {
            columnText = file.getName();
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_GROUP) {
            columnText = file.getGroup();
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_CATEGORY1) {
            columnText = file.getCategory1();
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_CATEGORY2) {
            columnText = file.getCategory2();
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_PRIORITY) {
            columnText = file.getPriority();
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_LOCATION) {
            columnText = file.getLocation();
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_MORECOMMANDS) {
            columnText = file.getMoreCommands();
            if (columnText != null) {
                columnText = columnText.replaceAll("\r", "");
                columnText = columnText.replaceAll("\n", " ; ");
            }
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_WORKING_DIR) {
            columnText = file.getWorkingDir();
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_SIZE) {
            long size = file.getSize();
            columnText = size >= 0 ? Long.toString(size) : "-";
        } else if (columnIndex == ShortcutsLabelProvider.COL_INDEX_LAST_MODIFIED) {
            long lastm = file.getLastModified();
            DateFormat dataformat = DateFormat.getDateInstance(1);
            columnText = lastm >= 0 ? dataformat.format(Long.valueOf(lastm)) : "-";
        }
        return columnText;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        Shortcut shortcut = (Shortcut) element;

        if (columnIndex == 0) {
            String rgb = shortcut.getRgb() != null ? shortcut.getRgb() : UIConstants.DEFAULT_SHORTCUT_RGB;

            Image img = ShortcutsLabelProvider.imageRegistry.get("$" + rgb);

            if (img == null) {
                RGB argb = UIUtils.decodeColor(rgb);
                img = this.createColoredImage(argb.red, argb.green, argb.blue);
                ShortcutsLabelProvider.imageRegistry.put("$" + rgb, img);
            }

            return img;
        } else if (columnIndex == 1) {
            Image img = this.getShortcutImage(shortcut);
            return img;
        }
        return null;
    }

    @Override
    public Image getImage(Object element) {
        Image img = this.getShortcutImage((Shortcut) element);
        return img;
    }

    private Image getShortcutImage(Shortcut shortcut) {
        String location = shortcut.getLocation();
        int dotPosition = shortcut.getLocation().lastIndexOf(".");
        String extension = dotPosition == -1 ? "" : location.substring(dotPosition);
        return ShortcutsLabelProvider.getIcon(extension);
    }

    private static Image getIcon(String extension) {
        // if (ShortcutsLabelProvider.imageRegistry == null) {
        // ShortcutsLabelProvider.imageRegistry = new ImageRegistry();
        // }
        Image image = ShortcutsLabelProvider.imageRegistry.get(extension);
        if (image != null) {
            return image;
        }
        Program program = Program.findProgram(extension);
        ImageData imageData = program == null ? null : program.getImageData();
        if (imageData != null) {
            image = new Image(Display.getCurrent(), imageData);
            ShortcutsLabelProvider.imageRegistry.put(extension, image);
        } else {
            image = ShortcutsLabelProvider.imageRegistry.get("$ICON_SHORTCUT");
            if (image == null) {
                image = Activator.getImageDescriptor(UIConstants.ICON_SHORTCUT).createImage();
                ShortcutsLabelProvider.imageRegistry.put("$ICON_SHORTCUT", image);
            }
            // image = Activator.getImageDescriptor(UIConstants.ICON_SHORTCUT).createImage();
        }

        return image;
    }

    @Override
    public Color getBackground(Object element, int columnIndex) {
        // return new Color(Display.getCurrent(), 100, 100, 250);
        // TODO : Color muss disposed werden!!!

        // if (columnIndex > 1) {
        // return Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
        // }
        return null;
    }

    @Override
    public Color getForeground(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dispose() {
        // TODO Dispose Images? => No, s. ImageRegistry
        super.dispose();
    }

    private Image createColoredImage(int rFactor, int gFactor, int bFactor) {
        // ImageData ideaImageData = new ImageData(this.getClass().getResourceAsStream("/icons/bookmark.png"));
        Image s = ShortcutsLabelProvider.imageRegistry.get("$ICON_SHORTCUT_BW");
        if (s == null) {
            s = Activator.getImageDescriptor(UIConstants.ICON_SHORTCUT_BW).createImage();
            ShortcutsLabelProvider.imageRegistry.put("$ICON_SHORTCUT_BW", s);
        }

        ImageData imageData = s.getImageData();

        int redMask = imageData.palette.redMask, greenMask = imageData.palette.greenMask, blueMask = imageData.palette.blueMask;
        int redShift = imageData.palette.redShift, greenShift = imageData.palette.greenShift, blueShift = imageData.palette.blueShift;

        redShift = Math.abs(redShift);
        greenShift = Math.abs(greenShift);
        blueShift = Math.abs(blueShift);

        int[] lineData = new int[imageData.width];
        for (int y = 0; y < imageData.height; y++) {
            imageData.getPixels(0, y, imageData.width, lineData, 0);
            // Analyze each pixel value in the line
            for (int x = 0; x < lineData.length; x++) {
                // Extract the red, green and blue component
                int pixelValue = lineData[x];
                int r = (pixelValue & redMask) >> redShift;
            int g = (pixelValue & greenMask) >> greenShift;
            int b = (pixelValue & blueMask) >> blueShift;

            float fr = (float) rFactor / 255;
            float fg = (float) gFactor / 255;
            float fb = (float) bFactor / 255;

            r = (int) (r * fr);
            g = (int) (g * fg);
            b = (int) (b * fb);

            int ic = ((r << redShift) & redMask) + ((g << greenShift) & greenMask) + ((b << blueShift) & blueMask);

            imageData.setPixel(x, y, ic);

            }
        }

        return new Image(Display.getCurrent(), imageData);

    }

}
