package uk.ac.ebi.pride.gui.component.table.model;

import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.sequence.Protein;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.impl.RetrieveSequenceCoverageTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IdentificationTableModel stores all information to be displayed in the identification table.
 * <p/>
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:04
 */
public class ProteinTableModel extends ProgressiveUpdateTableModel<Void, Tuple<TableContentType, Object>> {


    /**
     * table column title
     */
    public enum TableHeader {
        ROW_NUMBER_COLUMN("Row", "Row Number"),
        PROTEIN_ACCESSION_COLUMN("Submitted Protein Accession", "Submitted Protein Accession From Source"),
        MAPPED_PROTEIN_ACCESSION_COLUMN("Mapped Protein Accession", "Pride Mapped Protein Accession"),
        PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
        PROTEIN_SEQUENCE_COVERAGE("Sequence Coverage", "Protein Sequence Coverage"),
        IDENTIFICATION_SCORE_COLUMN("Score", "PRIDE Protein Score"),
        IDENTIFICATION_THRESHOLD_COLUMN("Threshold", "PRIDE Protein Threshold"),
        NUMBER_OF_PEPTIDES("# Peptides", "Number of Peptides"),
        NUMBER_OF_UNIQUE_PEPTIDES("# Distinct Peptides", "Number of Distinct Peptides"),
        NUMBER_OF_PTMS("# PTMs", "Number of PTMs"),
        IDENTIFICATION_ID("Identification ID", "Identification ID");

        private final String header;
        private final String toolTip;

        private TableHeader(String header, String tooltip) {
            this.header = header;
            this.toolTip = tooltip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    /**
     * the data access controller related to this model
     */
    private DataAccessController controller;

    public ProteinTableModel() {
        this(null);
    }

    public ProteinTableModel(DataAccessController controller) {
        this.controller = controller;
    }

    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.IDENTIFICATION.equals(type)) {
            addIdentificationData(newData.getValue());
        } else if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetailData(newData.getValue());
        } else if (TableContentType.PROTEIN_SEQUENCE_COVERAGE.equals(type)) {
            addSequenceCoverageData(newData.getValue());
        }
    }

    /**
     * Add identification detail for each row
     *
     * @param newData identification detail
     */
    private void addIdentificationData(Object newData) {
        List<Object> content = new ArrayList<Object>();
        int rowCnt = this.getRowCount();
        // row number
        content.add(rowCnt + 1);
        content.addAll((List<Object>) newData);
        contents.add(content);
        fireTableRowsInserted(rowCnt, rowCnt);
    }

    /**
     * Add protein detail data
     *
     * @param newData protein detail map
     */
    private void addProteinDetailData(Object newData) {
        // column index for mapped protein accession column
        int mappedAccIndex = getColumnIndex(TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        // column index for protein name
        int identNameIndex = getColumnIndex(TableHeader.PROTEIN_NAME.getHeader());
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());

        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) newData;
        // A list of protein identification id which can get sequence coverage
        List<Comparable> identIds = new ArrayList<Comparable>();

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
            Object mappedAcc = content.get(mappedAccIndex);
            if (mappedAcc != null) {
                Protein protein = proteins.get(mappedAcc);
                if (protein != null) {
                    // set protein name
                    content.set(identNameIndex, protein.getName());
                    // add protein identification id to the list
                    identIds.add((Comparable) content.get(identIdIndex));
                    // notify a row change
                    fireTableRowsUpdated(row, row);
                }
            }
        }

        // retrieve protein sequence coverages
        Task task = new RetrieveSequenceCoverageTask(identIds, controller);
        task.addTaskListener(this);
        task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));
        Desktop.getInstance().getDesktopContext().addTask(task);
    }

    /**
     * Add protein sequence coverages
     *
     * @param newData sequence coverage map
     */
    private void addSequenceCoverageData(Object newData) {
        // column index for protein identification id
        int identIdIndex = getColumnIndex(TableHeader.IDENTIFICATION_ID.getHeader());
        // column index for protein sequence coverage
        int coverageIndex = getColumnIndex(TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());

        // map contains sequence coverage
        Map<Comparable, Double> coverageMap = (Map<Comparable, Double>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            List<Object> content = contents.get(row);
            Object identId = content.get(identIdIndex);
            Double coverage = coverageMap.get(identId);
            if (coverage != null) {
                // set protein name
                content.set(coverageIndex, coverage);
                // notify a row change
                fireTableCellUpdated(row, coverageIndex);
            }
        }
    }
}
