package uk.ac.ebi.pride.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.impl.MzMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.OpenFileTask;
import uk.ac.ebi.pride.gui.task.impl.OpenGzippedFileTask;
import uk.ac.ebi.pride.gui.utils.Constants;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * OpenFileAction opens files supported by PRIDE Viewer
 * so far: mzML, PRIDE XML
 * <p/>
 * User: rwang
 * Date: 18-Aug-2010
 * Time: 11:40:33
 */
public class OpenFileAction extends PrideAction implements TaskListener<Void, File> {
    private static final Logger logger = LoggerFactory.getLogger(OpenFileAction.class);

    private List<File> inputFilesToOpen;
    private PrideInspectorContext context;

    public OpenFileAction(String name, Icon icon) {
        this(name, icon, null);
        setAccelerator(java.awt.event.KeyEvent.VK_O, ActionEvent.CTRL_MASK);
    }

    public OpenFileAction(String name, Icon icon, List<File> files) {
        super(name, icon);
        inputFilesToOpen = files == null ? null : new ArrayList<File>(files);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        // create a open file dialog
        List<File> filesToOpen = inputFilesToOpen == null ? createFileOpenDialog() : inputFilesToOpen;

        // unzip zipped files
        unzipFiles(filesToOpen);

        // show warning messages if the file is too big
        boolean choice = showBigFileWarningMessage(filesToOpen);

        if (choice) {
            // open unzipped files
            openFiles(filesToOpen);
        }
    }

    /**
     * Show a warning message if the file size is over certain threshold
     * @param files a list of input files
     * @return  boolean open files if returns true
     */
    private boolean showBigFileWarningMessage(List<File> files) {
        boolean tooBig = false;
        boolean toOpen = true;

        // get the threshold for file size first
        long fileSizeThreshold = Long.parseLong(context.getProperty("open.file.threshold"));

        // check the size of each file
        for (File file : files) {
            // get the length in bytes
            long length = file.length();
            if ((length / (1024 * 1024)) > fileSizeThreshold) {
                tooBig = true;
                break;
            }
        }

        if (tooBig) {
            // check whether the user still want to open
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(Desktop.getInstance().getMainComponent(),
                    "Selected File is over " + fileSizeThreshold + "M in size, it will take longer to open, would you like to continue?",
                    "Big File Found",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
            toOpen = (n == JOptionPane.YES_OPTION);
        }

        return toOpen;
    }

    /**
     * Unzip files
     *
     * @param files a list of input files
     */
    private void unzipFiles(List<File> files) {
        if (hasGzipFiles(files)) {
            // separate files to unzipped and zipped
            List<File> zippedFiles = new ArrayList<File>();

            for (File file : files) {
                if (isGzipFile(file)) {
                    zippedFiles.add(file);
                }
            }

            // remove all the zipped files from the list
            files.removeAll(zippedFiles);

            // check whether the user want to unzip
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(Desktop.getInstance().getMainComponent(),
                    "Would you like to unzip compressed files before loading?",
                    "Gzip Files Found",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[1]);

            if (n == JOptionPane.YES_OPTION) {
                // ask for the path to save unzipped files
                JFileChooser ofd = new JFileChooser(context.getOpenFilePath());
                ofd.setDialogTitle("Select folder to save unzipped files");
                ofd.setDialogType(JFileChooser.OPEN_DIALOG);
                ofd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                ofd.setMultiSelectionEnabled(false);

                int result = ofd.showOpenDialog(Desktop.getInstance().getMainComponent());
                if (result == JFileChooser.APPROVE_OPTION) {
                    File path = ofd.getSelectedFile();
                    // start new tasks to unzip files
                    openGzippedFiles(zippedFiles, path.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Check whether a list of files contains gzip files.
     *
     * @param files a list of input files
     * @return boolean true means there is gzip files
     */
    private boolean hasGzipFiles(List<File> files) {
        boolean hasGzip = false;

        for (File file : files) {
            if (isGzipFile(file)) {
                hasGzip = true;
                break;
            }
        }

        return hasGzip;
    }

    /**
     * Check whether a file is gzip file based its extension.
     *
     * @param file input file
     * @return boolean true means it is a gzip file
     */
    private boolean isGzipFile(File file) {
        return file.getName().endsWith(".gz");
    }

    /**
     * create a file open dialog if not input files has been specified
     *
     * @return List<File>  a list of input files
     */
    private List<File> createFileOpenDialog() {

        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select mzML/PRIDE xml Files", null, true,
                                            Constants.MZXML_FILE, Constants.XML_FILE, Constants.GZIPPED_FILE);
        int result = ofd.showDialog(Desktop.getInstance().getMainComponent(), null);

        List<File> filesToOpen = new ArrayList<File>();

        // check the selection results from open fiel dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            filesToOpen.addAll(Arrays.asList(ofd.getSelectedFiles()));
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            // remember the path has visited
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
        }

        return filesToOpen;
    }

    /**
     * <code> openFiles </code> opens a list of files.
     *
     * @param files files to open
     */
    @SuppressWarnings("unchecked")
    private void openFiles(List<File> files) {
        for (File selectedFile : files) {
            // check the file type
            Class classType = null;
            try {
                classType = getFileType(selectedFile);
            } catch (IOException e1) {
                logger.error("Failed to check the file type", e1);
            }

            if (classType != null) {
                String msg = "Opening " + selectedFile.getName();
                OpenFileTask newTask = new OpenFileTask(selectedFile, classType, msg, msg);
                // set task's gui blocker
                newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                // add task listeners
                // ToDo: this why we need a singleton DesktopContext
                Desktop.getInstance().getDesktopContext().addTask(newTask);
            }
        }
    }

    private void openGzippedFiles(List<File> files, String path) {
        OpenGzippedFileTask newTask = new OpenGzippedFileTask(files, path);

        // listen this this task
        newTask.addTaskListener(this);

        // set task's gui blocker
        newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));

        // add task listeners
        // ToDo: this why we need a singleton DesktopContext
        Desktop.getInstance().getDesktopContext().addTask(newTask);
    }

    /**
     * Check the file type
     *
     * @param file input file
     * @return Class    the class type of the data access controller
     * @throws IOException exception while checking the file type
     */
    private Class getFileType(File file) throws IOException {
        Class classType = null;

        // check file type
        if (MzMLControllerImpl.isValidFormat(file)) {
            classType = MzMLControllerImpl.class;
        } else if (PrideXmlControllerImpl.isValidFormat(file)) {
            classType = PrideXmlControllerImpl.class;
        } else {
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), "<html><h4>The files you selected are not in supported format.</h4> The formats are supported by PRIDE Inspector are: <br> <b> PRIDE XML </b> <br> <b> mzML </b> </html>", "Wrong File Format");
        }

        return classType;
    }

    @Override
    public void process(TaskEvent<List<File>> listTaskEvent) {
        openFiles(listTaskEvent.getValue());
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<Void> voidTaskEvent) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
