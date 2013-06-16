package uk.ac.ebi.pride.gui.component.table;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.TreeTableModel;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.component.table.listener.*;
import uk.ac.ebi.pride.gui.component.table.model.*;
import uk.ac.ebi.pride.gui.component.table.renderer.*;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.url.*;
import uk.ac.ebi.pride.gui.utils.Constants;
import uk.ac.ebi.pride.term.CvTermReference;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TableFactory can be used to different type of tables.
 * <p/>
 * User: rwang
 * Date: 11-Sep-2010
 * Time: 13:39:00
 */
public class TableFactory {
    /**
     * Build a table to display spectrum related details
     *
     * @return JTable   spectrum table
     */
    public static JTable createSpectrumTable() {
        return createDefaultJXTable(new SpectrumTableModel(), new DefaultTableColumnModelExt());
    }

    /**
     * Build a table to display chromatogram related details.
     *
     * @return JTable   chromatogram table
     */
    public static JTable createChromatogramTable() {
        return createDefaultJXTable(new ChromatogramTableModel(), new DefaultTableColumnModelExt());
    }

    /**
     * Build a table to display identification related details.
     *
     * @param controller data access controller
     * @return JTable   identification table
     */
    public static JTable createIdentificationTable(Collection<CvTermReference> listProteinScores, DataAccessController controller) {

        ProteinTableModel identTableModel = new ProteinTableModel(listProteinScores);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable table = createDefaultJXTable(identTableModel, columnModel);

        TableColumnExt proteinIdColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        TableColumnExt proteinNameColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_NAME.getHeader());
        // set protein name width
        proteinNameColumn.setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt seqCoverageColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        seqCoverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        seqCoverageColumn.setVisible(false);

        // isoelectric point column
        TableColumnExt isoelectricColumn = (TableColumnExt) table.getColumn(ProteinTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader());
        isoelectricColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = ProteinTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumn protAcc = table.getColumn(ProteinTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
        protAcc.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());

        // additional column
        String additionalColHeader = ProteinTableModel.TableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) table.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);


        // add mouse motion listener
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, additionalColHeader));
        table.addMouseListener(new ShowParamsMouseListener(controller, table, additionalColHeader));

        return table;
    }

    /**
     * Build a table to display peptide related details.
     *
     * @param listPeptideScores List of Reference Scores
     * @return JTable   peptide table
     */
    public static JXTreeTable createPeptideTreeTable(Collection<CvTermReference> listPeptideScores, int defaultRankingThreshold) {

        PeptideTreeTableModel peptideTableModel = new PeptideTreeTableModel(listPeptideScores, defaultRankingThreshold);
        JXTreeTable table = createDefaultJXTreeTable(peptideTableModel);

        table.setColumnFactory(new ColumnFactory() {
            @Override
            public void configureTableColumn(TableModel model, TableColumnExt columnExt) {
                super.configureTableColumn(model, columnExt);

                // peptide sequence column renderer
                String columnTitle = columnExt.getTitle();
                // set column visibility
                if (PeptideTreeTableModel.TableHeader.SPECTRUM_ID.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.IDENTIFICATION_ID.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.PEPTIDE_FIT.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.PEPTIDE_ID.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.PROTEIN_STATUS.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader().equals(columnTitle) ||
                        PeptideTreeTableModel.TableHeader.ADDITIONAL.getHeader().equals(columnTitle)) {
                    columnExt.setVisible(false);
                }

                // peptide sequence column renderer
                if (PeptideTreeTableModel.TableHeader.PEPTIDE_COLUMN.getHeader().equals(columnTitle)) {
                    columnExt.setMinWidth(200);
                }

                // delta mass column
                if (PeptideTreeTableModel.TableHeader.DELTA_MASS_COLUMN.getHeader().equals(columnTitle)) {
                    double minLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
                    double maxLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
                    DeltaMZRenderer renderer = new DeltaMZRenderer(minLimit, maxLimit);
                    columnExt.setCellRenderer(renderer);
                }

                // peptide sequence present in protein sequence
                if (PeptideTreeTableModel.TableHeader.PEPTIDE_FIT.getHeader().equals(columnTitle)) {
                    columnExt.setCellRenderer(new PeptideFitCellRenderer());
                }

                // set protein name column width
                if (PeptideTreeTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(columnTitle)) {
                    columnExt.setPreferredWidth(200);
                }

                // sequence coverage column
                if (PeptideTreeTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(columnTitle)) {
                    columnExt.setCellRenderer(new SequenceCoverageRenderer());
                }

                // ptm accession hyperlink
                if (PeptideTreeTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader().equals(columnTitle)) {
                    columnExt.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());
                }

                // set additional column
                if (PeptideTreeTableModel.TableHeader.ADDITIONAL.getHeader().equals(columnTitle)) {
                    Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
                    columnExt.setCellRenderer(new IconRenderer(icon));
                    columnExt.setMaxWidth(50);
                }
            }
        });

        // add hyper link click listener
        String protAccColumnHeader = PeptideTreeTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        peptideTableModel.getTreeModelSupport().fireTreeStructureChanged(new TreePath(peptideTableModel.getRoot()));

        return table;
    }


    /**
     * Build a table to display peptide related details.
     *
     * @param listPeptideScores List of Reference Scores
     * @param controller        data access controller
     * @return JTable   peptide table
     */
    public static JTable createPeptideTable(Collection<CvTermReference> listPeptideScores, DataAccessController controller) {

        PeptideTableModel peptideTableModel = new PeptideTableModel(listPeptideScores);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable table = createDefaultJXTable(peptideTableModel, columnModel);

        // peptide sequence column renderer
        // peptide sequence column renderer
        TableColumnExt peptideColumn = (TableColumnExt) table.getColumn(PeptideTreeTableModel.TableHeader.PEPTIDE_COLUMN.getHeader());
        peptideColumn.setMinWidth(200);

        // delta mass column
        TableColumnExt deltaMassColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.DELTA_MASS_COLUMN.getHeader());
        double minLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
        double maxLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
        DeltaMZRenderer renderer = new DeltaMZRenderer(minLimit, maxLimit);
        deltaMassColumn.setCellRenderer(renderer);

        // peptide sequence present in protein sequence
        TableColumnExt peptideFitColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader());
        peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());
        peptideFitColumn.setVisible(false);

        // hide protein id column
        TableColumnExt proteinIdColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide peptide id column
        TableColumnExt peptideIdColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_ID.getHeader());
        peptideIdColumn.setVisible(false);

        // set protein name column width
        TableColumnExt proteinNameColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setPreferredWidth(200);

        // hide the protein name column
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt coverageColumn = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        coverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        coverageColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = PeptideTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, protAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumnExt protAcc = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
        protAcc.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());

        // set peptide column width
        peptideColumn.setPreferredWidth(200);

        // hide number of fragment ions
        TableColumnExt numOfFragmentIons = (TableColumnExt) table.getColumn(PeptideTableModel.TableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader());
        numOfFragmentIons.setVisible(false);

        // hide spectrum id column
        String spectrumIdHeader = PeptideTableModel.TableHeader.SPECTRUM_ID.getHeader();
        TableColumnExt spectrumIdColumn = (TableColumnExt) table.getColumn(spectrumIdHeader);
        spectrumIdColumn.setVisible(false);

        // additional column
        String additionalColHeader = PeptideTableModel.TableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) table.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        // hide pI column
        String pIHeader = PeptideTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader();
        TableColumnExt pICol = (TableColumnExt) table.getColumn(pIHeader);
        pICol.setVisible(false);

        // add mouse motion listener
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, additionalColHeader));
        table.addMouseListener(new ShowParamsMouseListener(controller, table, additionalColHeader));

        return table;
    }

    /**
     * Build a table to display PTM related details.
     *
     * @return JTable   ptm table
     */
    public static JTable createPTMTable() {
        PTMTableModel tableModel = new PTMTableModel();
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable table = createDefaultJXTable(tableModel, columnModel);

        // add hyper link click listener
        String modAccColumnHeader = PTMTableModel.TableHeader.PTM_ACCESSION.getHeader();
        table.addMouseMotionListener(new TableCellMouseMotionListener(table, modAccColumnHeader));
        table.addMouseListener(new HyperLinkCellMouseClickListener(table, modAccColumnHeader, new PTMHyperLinkGenerator()));

        // ptm accession hyperlink
        TableColumnExt ptmColumn = (TableColumnExt) table.getColumn(PTMTableModel.TableHeader.PTM_ACCESSION.getHeader());
        ptmColumn.setCellRenderer(new HyperLinkCellRenderer());

        return table;
    }

    /**
     * Build a table to display database search summaries.
     *
     * @return JTable  database search table
     */
    public static JTable createDatabaseSearchTable() {
        DatabaseSearchTableModel tableModel = new DatabaseSearchTableModel();
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable searchTable = createDefaultJXTable(tableModel, columnModel);

        // add cell renderer to view column
        String viewColumnHeader = DatabaseSearchTableModel.TableHeader.VIEW.getHeader();
        TableColumnExt viewColumn = (TableColumnExt) searchTable.getColumn(viewColumnHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("add.experiment.small.icon"));
        viewColumn.setCellRenderer(new IconRenderer(icon));
        viewColumn.setMaxWidth(50);

        // pubmed column
        String pubmedHeader = DatabaseSearchTableModel.TableHeader.PUBMED_ID.getHeader();
        TableColumnExt pubmedColumn = (TableColumnExt) searchTable.getColumn(pubmedHeader);
        Pattern pubmedPattern = Pattern.compile("[\\d,]+");
        pubmedColumn.setCellRenderer(new HyperLinkCellRenderer(pubmedPattern));

        // hide taxonomy_id and brenda_id column
        String taxonomyIdHeader = DatabaseSearchTableModel.TableHeader.TAXONOMY_ID.getHeader();
        TableColumnExt taxonomyIdColumn = (TableColumnExt) searchTable.getColumn(taxonomyIdHeader);
        Pattern taxonomyIdPattern = Pattern.compile("[\\d]+;{0}");
        taxonomyIdColumn.setVisible(false);

        String brendaIdHeader = DatabaseSearchTableModel.TableHeader.BRENDA_ID.getHeader();
        TableColumnExt brendaIdColumn = (TableColumnExt) searchTable.getColumn(brendaIdHeader);
        Pattern brendaIdPattern = Pattern.compile("BTO:\\d+");
        brendaIdColumn.setVisible(false);

        // add hyper link for species and tissue column
        String speciesHeader = DatabaseSearchTableModel.TableHeader.SPECIES.getHeader();
        TableColumnExt speciesColumn = (TableColumnExt) searchTable.getColumn(speciesHeader);
        Pattern speciesPattern = Pattern.compile(".+");
        speciesColumn.setCellRenderer(new HyperLinkCellRenderer(speciesPattern));

        String tissueHeader = DatabaseSearchTableModel.TableHeader.TISSUE.getHeader();
        TableColumnExt tissueColumn = (TableColumnExt) searchTable.getColumn(tissueHeader);
        Pattern tissuePattern = Pattern.compile(".+");
        tissueColumn.setCellRenderer(new HyperLinkCellRenderer(tissuePattern));

        // add mouse motion listener
        searchTable.addMouseMotionListener(new TableCellMouseMotionListener(searchTable, viewColumnHeader, pubmedHeader));
        searchTable.addMouseListener(new OpenExperimentMouseListener(searchTable, viewColumnHeader));
        searchTable.addMouseListener(new HyperLinkCellMouseClickListener(searchTable, pubmedHeader, new PrefixedHyperLinkGenerator(Constants.PUBMED_URL_PERFIX), pubmedPattern));
        searchTable.addMouseListener(new HyperLinkCellMouseClickListener(searchTable, speciesHeader, taxonomyIdHeader, new PrefixedHyperLinkGenerator(Constants.OLS_URL_PREFIX), taxonomyIdPattern));
        searchTable.addMouseListener(new HyperLinkCellMouseClickListener(searchTable, tissueHeader, brendaIdHeader, new PrefixedHyperLinkGenerator(Constants.OLS_URL_PREFIX), brendaIdPattern));

        return searchTable;
    }

    /**
     * Create a table to show a list of references
     *
     * @param references a list of input references
     * @return JTable  reference table
     */
    public static JTable createReferenceTable(Collection<Reference> references) {
        ReferenceTableModel tableModel = new ReferenceTableModel(references);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable referenceTable = createDefaultJXTable(tableModel, columnModel);

        // pubmed
        String pubMedColumnHeader = ReferenceTableModel.TableHeader.PUBMED.getHeader();
        TableColumnExt pubMedColumn = (TableColumnExt) referenceTable.getColumn(pubMedColumnHeader);
        Pattern pubmedPattern = Pattern.compile("[\\d,]+");
        pubMedColumn.setCellRenderer(new HyperLinkCellRenderer(pubmedPattern));
        pubMedColumn.setMaxWidth(100);

        // doi
        String doiColumnHeader = ReferenceTableModel.TableHeader.DOI.getHeader();
        TableColumnExt doiColumn = (TableColumnExt) referenceTable.getColumn(doiColumnHeader);
        doiColumn.setCellRenderer(new HyperLinkCellRenderer());
        doiColumn.setMaxWidth(100);

        // reference
        String referenceColumnHeader = ReferenceTableModel.TableHeader.REFERENCE_DESCRIPTION.getHeader();

        // add mouse motion listener
        referenceTable.addMouseMotionListener(new TableCellMouseMotionListener(referenceTable, pubMedColumnHeader, doiColumnHeader, referenceColumnHeader));
        referenceTable.addMouseListener(new HyperLinkCellMouseClickListener(referenceTable, pubMedColumnHeader, new PrefixedHyperLinkGenerator(Constants.PUBMED_URL_PERFIX), pubmedPattern));
        referenceTable.addMouseListener(new HyperLinkCellMouseClickListener(referenceTable, doiColumnHeader, new DOIHyperLinkGenerator(Constants.DOI_URL_PREFIX)));
        Collection<String> columnHeadersWithPopup = new HashSet<String>();
        columnHeadersWithPopup.add(referenceColumnHeader);
        referenceTable.addMouseListener(new MouseClickPopupListener(referenceTable, columnHeadersWithPopup));

        return referenceTable;
    }

    /**
     * Create a table for showing a list of param groups
     *
     * @param paramGroups given list of param groups
     * @return JTable  param table
     */
    public static JTable createParamTable(List<ParamGroup> paramGroups) {
        ParamTableModel paramTableModel = new ParamTableModel(paramGroups);
        return createParamTable(paramTableModel);
    }

    /**
     * Create a table for showing a ParamGroup
     *
     * @param paramGroup given ParamGroup
     * @return JTable  param table
     */
    public static JTable createParamTable(ParamGroup paramGroup) {
        ParamTableModel paramTableModel = new ParamTableModel(paramGroup);
        return createParamTable(paramTableModel);
    }

    /**
     * Create a table for showing a collection parameters
     *
     * @param parameters a collection of parameters
     * @return JTable  param table
     */
    public static JTable createParamTable(Collection<Parameter> parameters) {
        ParamTableModel paramTableModel = new ParamTableModel(parameters);
        return createParamTable(paramTableModel);
    }


    /**
     * Create a table for showing a ParamTableModel
     *
     * @param paramTableModel given param table model
     * @return JTable  param table
     */
    private static JTable createParamTable(ParamTableModel paramTableModel) {
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable paramTable = createDefaultJXTable(paramTableModel, columnModel);

        // hyperlink ontology accessions
        String accColumnHeader = ParamTableModel.TableHeader.ACCESSION.getHeader();
        TableColumnExt accColumn = (TableColumnExt) paramTable.getColumn(accColumnHeader);
        accColumn.setCellRenderer(new HyperLinkCellRenderer());

        // add mouse motion listener
        paramTable.addMouseMotionListener(new TableCellMouseMotionListener(paramTable, accColumnHeader));
        paramTable.addMouseListener(new HyperLinkCellMouseClickListener(paramTable, accColumnHeader, new PrefixedHyperLinkGenerator(Constants.OLS_URL_PREFIX)));

        // acc column hidden
        String accHeader = ParamTableModel.TableHeader.ACCESSION.getHeader();
        TableColumnExt accCol = (TableColumnExt) paramTable.getColumn(accHeader);
        accCol.setVisible(false);


        return paramTable;
    }

    /**
     * Create a table for showing contacts
     *
     * @param contacts given list of contacts
     * @return JTable  contact table
     */
    public static JTable createContactTable(Collection<Person> contacts) {

        ContactTableModel tableModel = new ContactTableModel(contacts);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable contactTable = createDefaultJXTable(tableModel, columnModel);

        // hyperlink contact emails
        String infoColumnHeader = ContactTableModel.TableHeader.INFORMATION.getHeader();
        TableColumnExt infoColumn = (TableColumnExt) contactTable.getColumn(infoColumnHeader);
        infoColumn.setCellRenderer(new HyperLinkCellRenderer());

        // add mouse motion listener
        contactTable.addMouseMotionListener(new TableCellMouseMotionListener(contactTable, infoColumnHeader));
        contactTable.addMouseListener(new HyperLinkCellMouseClickListener(contactTable, infoColumnHeader, new EmailHyperLinkGenerator()));

        return contactTable;
    }

    /**
     * Create a table for quantitative sample data
     *
     * @param sample quantitative sample
     * @return JTable  Quantitative table
     */
    public static JTable createQuantSampleTable(QuantitativeSample sample) {
        QuantSampleTableModel tableModel = new QuantSampleTableModel(sample);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        return createDefaultJXTable(tableModel, columnModel);
    }

    /**
     * Create a table for quantitative protein data with a given table model
     *
     * @param tableModel quant protein table model
     * @return JTable  quant protein table
     */
    public static JTable createQuantProteinTable(DataAccessController controller, TableModel tableModel) {
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable quantProteinTable = createDefaultJXTable(tableModel, columnModel);
        quantProteinTable.setAutoCreateColumnsFromModel(false);
        // add table model change listener
        tableModel.addTableModelListener(new BarChartColumnListener(quantProteinTable));

        // in case the compare doesn't exist
        List<TableColumn> columns = columnModel.getColumns(true);
        for (TableColumn column : columns) {
            if (column.getHeaderValue().equals(QuantProteinTableModel.TableHeader.COMPARE.getHeader())) {
                column.setMaxWidth(25);
            }
        }
        // hide mapped protein accession
        String mappedProtAccHeader = QuantProteinTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        TableColumnExt mappedProtAccColumn = (TableColumnExt) quantProteinTable.getColumn(mappedProtAccHeader);
        mappedProtAccColumn.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());
        // add hyper link click listener
        quantProteinTable.addMouseMotionListener(new TableCellMouseMotionListener(quantProteinTable, mappedProtAccHeader));
        quantProteinTable.addMouseListener(new HyperLinkCellMouseClickListener(quantProteinTable, mappedProtAccHeader, new ProteinAccHyperLinkGenerator()));

        TableColumnExt proteinIdColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide the protein name column
        TableColumnExt proteinNameColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setVisible(false);

        // protein status column
        TableColumnExt proteinStatusColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // sequence coverage column
        TableColumnExt seqCoverageColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        seqCoverageColumn.setCellRenderer(new SequenceCoverageRenderer());
        seqCoverageColumn.setVisible(false);

        // isoelectric point column
        TableColumnExt isoelectricColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader());
        isoelectricColumn.setVisible(false);

        // score
        //TableColumnExt proteinScoreColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.IDENTIFICATION_SCORE_COLUMN.getHeader());
        //proteinScoreColumn.setVisible(false);

        // threshold
        TableColumnExt proteinThresholdColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.IDENTIFICATION_THRESHOLD_COLUMN.getHeader());
        proteinThresholdColumn.setVisible(false);

        // number of peptides
        TableColumnExt numOfPeptideColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.NUMBER_OF_PEPTIDES.getHeader());
        numOfPeptideColumn.setVisible(false);

        // number of unique peptides
        TableColumnExt numOfUniquePeptideColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.NUMBER_OF_UNIQUE_PEPTIDES.getHeader());
        numOfUniquePeptideColumn.setVisible(false);

        // number of ptms
        TableColumnExt numOfPtmColumn = (TableColumnExt) quantProteinTable.getColumn(QuantProteinTableModel.TableHeader.NUMBER_OF_PTMS.getHeader());
        numOfPtmColumn.setVisible(false);

        // additional column
        String additionalColHeader = ProteinTableModel.TableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) quantProteinTable.getColumn(additionalColHeader);
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(icon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        // add mouse motion listener
        quantProteinTable.addMouseMotionListener(new TableCellMouseMotionListener(quantProteinTable, additionalColHeader));
        quantProteinTable.addMouseListener(new ShowParamsMouseListener(controller, quantProteinTable, additionalColHeader));


        return quantProteinTable;
    }

    /**
     * Create a table for protein quantitative data
     *
     * @param controller data access controller
     * @return JTable   protein quantitative table
     */
    public static JTable createQuantProteinTable(DataAccessController controller, Collection<CvTermReference> listProteinScores) {
        QuantProteinTableModel tableModel = new QuantProteinTableModel(listProteinScores);
        return createQuantProteinTable(controller, tableModel);
    }

    /**
     * Create a table for peptide quantitative data
     *
     * @param listPeptideScores List of CvTerm
     * @return JTable  peptide table
     */
    public static JTable createQuantPeptideTable(DataAccessController controller, Collection<CvTermReference> listPeptideScores) {

        QuantPeptideTableModel tableModel = new QuantPeptideTableModel(listPeptideScores);
        DefaultTableColumnModelExt columnModel = new DefaultTableColumnModelExt();
        JXTable quantPeptideTable = createDefaultJXTable(tableModel, columnModel);
        quantPeptideTable.setAutoCreateColumnsFromModel(false);

        // add table model change listener
        tableModel.addTableModelListener(new BarChartColumnListener(quantPeptideTable));

        // hide protein accession
        String protAccHeader = QuantPeptideTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        TableColumnExt proteinAccColumn = (TableColumnExt) quantPeptideTable.getColumn(protAccHeader);
        proteinAccColumn.setVisible(false);

        // add hyper link click listener
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, protAccHeader));
        quantPeptideTable.addMouseListener(new HyperLinkCellMouseClickListener(quantPeptideTable, protAccHeader, new ProteinAccHyperLinkGenerator()));

        // hide protein name
        TableColumnExt proteinNameColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PROTEIN_NAME.getHeader());
        proteinNameColumn.setVisible(false);

        // hide protein status
        TableColumnExt proteinStatusColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PROTEIN_STATUS.getHeader());
        proteinStatusColumn.setVisible(false);

        // hide protein sequence coverage
        TableColumnExt proteinSeqCoverageColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());
        proteinSeqCoverageColumn.setVisible(false);

        // peptide sequence present in protein sequence
        TableColumnExt peptideFitColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader());
        peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());
        peptideFitColumn.setVisible(false);

        // precursor charge column
        TableColumnExt precursorChargeColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PRECURSOR_CHARGE_COLUMN.getHeader());
        precursorChargeColumn.setVisible(false);

        // delta mass column
        TableColumnExt deltaMassColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.DELTA_MASS_COLUMN.getHeader());
        deltaMassColumn.setVisible(false);
        double minLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
        double maxLimit = Double.parseDouble(Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
        DeltaMZRenderer renderer = new DeltaMZRenderer(minLimit, maxLimit);
        deltaMassColumn.setCellRenderer(renderer);

        // precursor m/z column
        TableColumnExt precursorMzColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PRECURSOR_MZ_COLUMN.getHeader());
        precursorMzColumn.setVisible(false);

        // hide number of fragment ions
        TableColumnExt fragIonsColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader());
        fragIonsColumn.setVisible(false);

        // hide peptide sequence length
        TableColumnExt seqLengthColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_SEQUENCE_LENGTH_COLUMN.getHeader());
        seqLengthColumn.setVisible(false);

        // hide sequence start
        TableColumnExt sequenceStartColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.SEQUENCE_START_COLUMN.getHeader());
        sequenceStartColumn.setVisible(false);

        // hide sequence end
        TableColumnExt sequenceEndColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.SEQUENCE_END_COLUMN.getHeader());
        sequenceEndColumn.setVisible(false);

        // hide pi point column
        TableColumnExt piColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.THEORITICAL_ISOELECTRIC_POINT_COLUMN.getHeader());
        piColumn.setVisible(false);

        // hide spectrum id
        TableColumnExt spectrumIdColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.SPECTRUM_ID.getHeader());
        spectrumIdColumn.setVisible(false);

        // hide protein id column
        TableColumnExt proteinIdColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
        proteinIdColumn.setVisible(false);

        // hide peptide id column
        TableColumnExt peptideIdColumn = (TableColumnExt) quantPeptideTable.getColumn(QuantPeptideTableModel.TableHeader.PEPTIDE_ID.getHeader());
        peptideIdColumn.setVisible(false);

        // add hyper link click listener
        String protAccColumnHeader = QuantPeptideTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, protAccColumnHeader));
        quantPeptideTable.addMouseListener(new HyperLinkCellMouseClickListener(quantPeptideTable, protAccColumnHeader, new ProteinAccHyperLinkGenerator()));

        // additional column
        String additionalColHeader = ProteinTableModel.TableHeader.ADDITIONAL.getHeader();
        TableColumnExt additionalCol = (TableColumnExt) quantPeptideTable.getColumn(additionalColHeader);
        Icon detailIcon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
        additionalCol.setCellRenderer(new IconRenderer(detailIcon));
        additionalCol.setMaxWidth(50);
        additionalCol.setVisible(false);

        // add mouse motion listener
        quantPeptideTable.addMouseMotionListener(new TableCellMouseMotionListener(quantPeptideTable, additionalColHeader));
        quantPeptideTable.addMouseListener(new ShowParamsMouseListener(controller, quantPeptideTable, additionalColHeader));


        return quantPeptideTable;
    }

    private static JXTable createDefaultJXTable(TableModel tableModel, TableColumnModel tableColumnModel) {
        JXTable table = new JXTable();

        if (tableColumnModel != null) {
            table.setColumnModel(tableColumnModel);
        }

        if (tableModel != null) {
            table.setModel(tableModel);
        }

        configureTable(table);

        return table;
    }

    private static JXTreeTable createDefaultJXTreeTable(TreeTableModel tableModel) {
        final JXTreeTable table = new JXTreeTable();

        // set table column model needs to happen before set tree table model
        table.setTreeTableModel(tableModel);
        table.setClosedIcon(null);
        table.setLeafIcon(null);
        table.setOpenIcon(null);

        configureTable(table);

        return table;
    }

    private static void configureTable(JXTable table) {
        JPopupMenu popupMenu = new TablePopupMenu(table);

        table.setComponentPopupMenu(popupMenu);

        // selection mode
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // set column control visible
        table.setColumnControlVisible(true);

        // auto fill
        table.setFillsViewportHeight(true);

        // sorter
        NumberTableRowSorter numberTableRowSorter = new NumberTableRowSorter(table.getModel());
        table.setRowSorter(numberTableRowSorter);

        // row height
        table.setRowHeight(20);

        // prevent dragging of column
        table.getTableHeader().setReorderingAllowed(false);

        // remove border
        table.setBorder(BorderFactory.createEmptyBorder());
    }
}