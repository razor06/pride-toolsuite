package uk.ac.ebi.pride.gui.component.protein;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.jdesktop.swingx.JXTreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.action.impl.ExtraProteinDetailAction;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableRow;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTreeTableModel;
import uk.ac.ebi.pride.gui.event.container.ExpandPanelEvent;
import uk.ac.ebi.pride.gui.event.container.ProteinIdentificationEvent;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * IdentificationSelectionPane displays identification related details in a table.
 * <p/>
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 10:20:51
 */
public class ProteinSelectionPane extends DataAccessControllerPane {

    private static final Logger logger = LoggerFactory.getLogger(ProteinTabPane.class.getName());

    /**
     * table for display the identifications
     */
    private JXTreeTable identTable;

    public ProteinSelectionPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    @Override
    protected void addComponents() {
        // create identification table
        identTable = TableFactory.createProteinTreeTable(controller.getAvailableProteinLevelScores(), controller.hasProteinAmbiguityGroup());

        // createAttributedSequence header panel
        JPanel headerPanel = buildHeaderPane();
        this.add(headerPanel, BorderLayout.NORTH);


        // add row selection listener
        TreeSelectionModel selectionModel = identTable.getTreeSelectionModel();
        selectionModel.addTreeSelectionListener(new IdentificationSelectionListener(identTable));

        // add identification table to scroll pane
        JScrollPane scrollPane = new JScrollPane(identTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPane() {
        // add meta data panel
        JPanel metaDataPanel = buildMetaDataPane();
        JToolBar buttonPanel = buildButtonPane();
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(metaDataPanel, BorderLayout.WEST);
        titlePanel.add(buttonPanel, BorderLayout.EAST);

        return titlePanel;
    }

    private JPanel buildMetaDataPane() {
        // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        try {
            // protein table label
            JLabel tableLabel = new JLabel("Protein");
            tableLabel.setFont(tableLabel.getFont().deriveFont(Font.BOLD));

            metaDataPanel.add(tableLabel);
            metaDataPanel.add(Box.createRigidArea(new Dimension(5, 0)));

            // identification type
//            Collection<Comparable> identIds = controller.getProteinIds();
//            Comparable identId = CollectionUtils.getElement(identIds, 0);

//            // search engine
//            Object engine = identId == null ? "Unknown" : getSearchEngineName(controller.getSearchEngineTypes());
//            engine = engine == null ? "Unknown" : engine;
//            JLabel dbLabel = new JLabel("<html><b>Search Engine</b>: " + engine + "</htlm>");
//            dbLabel.setToolTipText(engine.toString());
//            metaDataPanel.add(dbLabel);
//            metaDataPanel.add(Box.createRigidArea(new Dimension(5, 0)));
//
//            // search database
//            Object database = ((identId == null) || (controller.getSearchDatabase(identId).getName() == null)) ? "Unknown" : controller.getSearchDatabase(identId).getName();
//            database = database == null ? "Unknown" : database;
//            JLabel engineLabel = new JLabel("<html><b>Search Database</b>: </html>");
//            JLabel engineValLabel = new JLabel(database.toString());
//            engineValLabel.setPreferredSize(new Dimension(200, 15));
//            engineValLabel.setToolTipText(database.toString());
//            metaDataPanel.add(engineLabel);
//            metaDataPanel.add(engineValLabel);
        } catch (DataAccessException e) {
            String msg = "Failed to createAttributedSequence meta data pane for identifications";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }
        return metaDataPanel;
    }

    private String getSearchEngineName(Collection<SearchEngineType> searchEngineTypes) {
        StringBuilder builder = new StringBuilder();

        for (SearchEngineType searchEngineType : searchEngineTypes) {
            builder.append(searchEngineType.toString() + ",");
        }

        String searchEngineName = builder.toString();
        if (searchEngineName.length() > 1) {
            searchEngineName.substring(0, searchEngineName.length() - 1);
        }

        return searchEngineName;
    }

    /**
     * Build toolbar which contains all the buttons.
     *
     * @return JToolbar    tool bar
     */
    private JToolBar buildButtonPane() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        // load protein names
        JButton loadAllProteinNameButton = GUIUtilities.createLabelLikeButton(null, null);
        loadAllProteinNameButton.setForeground(Color.blue);

        loadAllProteinNameButton.setAction(new ExtraProteinDetailAction(controller));
        toolBar.add(loadAllProteinNameButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // decoy filter
//        JButton decoyFilterButton = GUIUtilities.createLabelLikeButton(null, null);
//        decoyFilterButton.setForeground(Color.blue);
//        PrideAction action = appContext.getPrideAction(controller, DecoyFilterAction.class);
//        if (action == null) {
//            action = new DecoyFilterAction(controller);
//            appContext.addPrideAction(controller, action);
//        }
//        decoyFilterButton.setAction(action);
//        toolBar.add(decoyFilterButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // expand button
        Icon expandIcon = GUIUtilities.loadIcon(appContext.getProperty("expand.table.icon.small"));
        JButton expandButton = GUIUtilities.createLabelLikeButton(expandIcon, null);
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventService eventBus = ContainerEventServiceFinder.getEventService(ProteinSelectionPane.this);
                eventBus.publish(new ExpandPanelEvent(ProteinSelectionPane.this, ProteinSelectionPane.this));
            }
        });

        toolBar.add(expandButton);

        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.protein");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        toolBar.add(helpButton);

        return toolBar;
    }

    /**
     * Return the identification table
     *
     * @return JTable  identification details table.
     */
    public JTable getIdentificationTable() {
        return identTable;
    }


    /**
     * This selection listener listens to Identification table for any selection on the row.
     * It will then fire a property change event with the selected identification id.
     */
    private class IdentificationSelectionListener implements TreeSelectionListener {
        private final JXTreeTable table;

        private IdentificationSelectionListener(JXTreeTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            int rowNum = table.getSelectedRow();
            int rowCnt = table.getRowCount();
            if (rowCnt > 0 && rowNum >= 0) {
                // get table model
                ProteinTreeTableModel tableModel = (ProteinTreeTableModel) identTable.getTreeTableModel();
                // fire a property change event with selected identification id
                TreeSelectionModel treeSelectionModel = table.getTreeSelectionModel();
                TreePath selectionPath = treeSelectionModel.getSelectionPath();
                Object node = selectionPath.getLastPathComponent();

                Comparable identId = ((ProteinTableRow)node).getProteinId();

                // publish the event to local event bus
                EventService eventBus = ContainerEventServiceFinder.getEventService(ProteinSelectionPane.this);
                eventBus.publish(new ProteinIdentificationEvent(ProteinSelectionPane.this, controller, identId));
            }
        }
    }
}
