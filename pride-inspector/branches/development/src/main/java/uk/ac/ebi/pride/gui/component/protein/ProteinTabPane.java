package uk.ac.ebi.pride.gui.component.protein;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.gui.component.mzgraph.MzGraphViewPane;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.task.TaskEvent;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * IdentTabPane displays protein identification and peptide related information.
 * It also visualize the spectrum peak list.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 16:23:02
 */
public class ProteinTabPane extends DataAccessControllerPane {

    private static final Logger logger = Logger.getLogger(MzDataTabPane.class.getName());

    /**
     * title
     */
    private static final String IDENTIFICATION_TITLE = "Protein";
    /**
     * resize weight for inner split pane
     */
    private static final double INNER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;
    /**
     * resize weight for outer split pane
     */
    private static final double OUTER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;
    /**
     * the size of the divider for split pane
     */
    private static final int DIVIDER_SIZE = 2;
    /**
     * Inner split pane contains peptide panel and mzgraph view pane
     */
    private JSplitPane innerPane;
    /**
     * Outer split pane contains inner split pane and protein panel
     */
    private JSplitPane outerPane;
    /**
     * Identification selection pane
     */
    private ProteinSelectionPane identPane;
    /**
     * Peptide selection pane
     */
    private PeptideSelectionPane peptidePane;
    /**
     * visualize mzgraph
     */
    private MzGraphViewPane mzViewPane;

    /**
     * Constructor
     *
     * @param controller data access controller
     * @param parentComp DataContentDisplayPane
     */
    public ProteinTabPane(DataAccessController controller, JComponent parentComp) {
        super(controller, parentComp);
    }

    /**
     * Setup the main display pane and its title
     */
    @Override
    protected void setupMainPane() {
        PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        this.setLayout(new BorderLayout());

        // set the title for the panel
        try {
            int ids = controller.getNumberOfIdentifications();
            String numIdent = " (" + ids + ")";

            this.setTitle(IDENTIFICATION_TITLE + numIdent);
        } catch (DataAccessException dex) {
            String msg = String.format("%s failed on : %s", this, dex);
            logger.log(Level.ERROR, msg, dex);
            context.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        // set the final icon
        this.setIcon(GUIUtilities.loadIcon(context.getProperty("identification.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(context.getProperty("identification.tab.loading.icon.small")));
    }

    /**
     * Add the rest components
     */
    @Override
    public void populate() {
        // create the inner split pane
        innerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        innerPane.setBorder(BorderFactory.createEmptyBorder());
        innerPane.setOneTouchExpandable(false);
        innerPane.setDividerSize(0);
        innerPane.setResizeWeight(INNER_SPLIT_PANE_RESIZE_WEIGHT);

        // create the outer split pane
        outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        outerPane.setBorder(BorderFactory.createEmptyBorder());
        outerPane.setOneTouchExpandable(false);
        outerPane.setDividerSize(DIVIDER_SIZE);
        outerPane.setResizeWeight(OUTER_SPLIT_PANE_RESIZE_WEIGHT);

        // protein identification selection pane
        identPane = new ProteinSelectionPane(controller);
        identPane.addPropertyChangeListener(this);
        outerPane.setTopComponent(identPane);

        // peptide selection pane
        peptidePane = new PeptideSelectionPane(controller);
        identPane.addPropertyChangeListener(peptidePane);
        innerPane.setTopComponent(peptidePane);

        // Spectrum view pane
        mzViewPane = new MzGraphViewPane(controller);
        mzViewPane.setVisible(false);
        peptidePane.addPropertyChangeListener(mzViewPane);
        peptidePane.addPropertyChangeListener(this);
        innerPane.setBottomComponent(mzViewPane);
        outerPane.setBottomComponent(innerPane);

        this.add(outerPane, BorderLayout.CENTER);
    }

    /**
     * Return a reference to the identification pane
     *
     * @return IdentificationSelectionPane  identification selection pane
     */
    public ProteinSelectionPane getIdentificationPane() {
        return identPane;
    }

    /**
     * Return a reference to the peptide pane
     *
     * @return PeptideSelectionPane peptide pane
     */
    public PeptideSelectionPane getPeptidePane() {
        return peptidePane;
    }

    /**
     * Triggered when a new mzgraph has been selected, it
     * sets the visibility of the mzViewPane
     *
     * @param evt property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (DataAccessController.MZGRAPH_TYPE.equals(evt.getPropertyName())) {
            // set the visibility of the mzViewPane
            MzGraph mzGraph = (MzGraph) evt.getNewValue();
            if (mzGraph == null || mzGraph.getBinaryDataArrays() == null) {
                mzViewPane.setVisible(false);
                innerPane.setDividerSize(0);
            } else {
                mzViewPane.setVisible(true);
                innerPane.setDividerSize(DIVIDER_SIZE);
            }

            // reset to preferred size
            outerPane.resetToPreferredSizes();
        } else if (ProteinSelectionPane.EXPAND_PROTEIN_PANEL.equals(evt.getPropertyName())) {
            boolean visible = innerPane.isVisible();
            if (visible) {
                innerPane.setVisible(false);
                outerPane.setDividerSize(0);
            } else {
                innerPane.setVisible(true);
                outerPane.setDividerSize(DIVIDER_SIZE);
            }
            outerPane.resetToPreferredSizes();
        }
    }

    @Override
    public void started(TaskEvent event) {
        showIcon(getLoadingIcon());
    }

    @Override
    public void finished(TaskEvent event) {
        showIcon(getIcon());
    }

    /**
     * Show a different icon if the parent component is not null and an instance of DataContentDisplayPane
     *
     * @param icon icon to show
     */
    private void showIcon(Icon icon) {
        if (parentComponent != null && parentComponent instanceof ControllerContentPane && icon != null) {
            ControllerContentPane contentPane = (ControllerContentPane) parentComponent;
            contentPane.setTabIcon(contentPane.getProteinTabIndex(), icon);
        }
    }
}