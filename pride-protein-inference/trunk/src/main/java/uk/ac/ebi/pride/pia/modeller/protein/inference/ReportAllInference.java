package uk.ac.ebi.pride.pia.modeller.protein.inference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.DBSequence;
import uk.ac.ebi.pride.data.core.Gel;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.PeptideEvidence;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.data.core.ProteinGroup;
import uk.ac.ebi.pride.data.core.Score;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.pia.intermediate.IntermediateGroup;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.pia.intermediate.IntermediateProtein;
import uk.ac.ebi.pride.pia.intermediate.IntermediateStructure;
import uk.ac.ebi.pride.pia.tools.LabelValueContainer;


/**
 * This inference filter reports all the PIA {@link IntermediateGroup}s as one protein.
 * <p>
 * This is similar to distinguish proteins simply by their peptides and report
 * every possible set and subset.
 * 
 * @author julian
 *
 */
public class ReportAllInference extends AbstractProteinInference {
	
	/** the logger for this class */
	private static final Logger logger= Logger.getLogger(ReportAllInference.class);
	
	/** the human readable name of this filter */
	protected static final String name = "Report All";
	
	/** the machine readable name of the filter */
	protected static final String shortName = "inference_report_all";
	
	/** the progress of the inference */
	private Double progress;
	
	
	public ReportAllInference(DataAccessController controller, int nrThreads) {
		super(controller, nrThreads);
		
		this.progress = 0.0;
	}
	
	
	@Override
	public List<LabelValueContainer<String>> getFilterTypes() {
		/*
		List<LabelValueContainer<String>> filters = new ArrayList<LabelValueContainer<String>>();
		
		filters.add(new LabelValueContainer<String>(null, "--- PSM ---"));
		for (Map.Entry<String, String>  scoreIt
				: getAvailableScoreShorts().entrySet()) {
			String[] filterNames = PSMScoreFilter.getShortAndFilteringName(
					scoreIt.getKey(), scoreIt.getValue());
			
			if (filterNames != null) {
				filters.add(new LabelValueContainer<String>(filterNames[0], filterNames[1]));
			}
		}
		filters.add(new LabelValueContainer<String>(NrPSMsPerPSMSetFilter.shortName(), NrPSMsPerPSMSetFilter.filteringName()));
		
		return filters;
		*/
		return null;
	}
	
	
	@Override
	public List<ProteinGroup> calculateInference( boolean considerModifications,
    		Map<String, Boolean> psmSetSettings) {
		/*
		List<ReportProtein> calculateInference(Map<Long, IntermediateGroup> groupMap,
			Map<String, ReportPSMSet> reportPSMSetMap,
			boolean considerModifications,
			Map<String, Boolean> psmSetSettings) {
		*/
		
		progress = 0.0;
		logger.info("calculateInference started...");
		/*
		logger.info("scoring: " + getScoring().getName() + " with " + 
				getScoring().getScoreSetting().getValue() + ", " +
				getScoring().getPSMForScoringSetting().getValue());
		*/
		
		// maps from the groups' IDs to the peptides, which should be reported
		Map<Integer, Set<IntermediatePeptide>> groupIdToReportPeptides =
			createFilteredPeptidesMap(considerModifications);
		
		// all the PSMs of the groups, including the PSMs in groups' children
		Map<Integer, Set<IntermediatePeptide>> groupsAllPeptides =
				new HashMap<Integer, Set<IntermediatePeptide>>(intermediateStructure.getNrGroups());
		
		// maps from the groups' IDs to the groups  with equal PSMs after filtering
		Map<Integer, Set<IntermediateGroup>> sameSets = null;
		
		// the finally returned list of protein groups
		List<ProteinGroup> proteinGroups = new ArrayList<ProteinGroup>();
		
		for (Set<IntermediateGroup> cluster : intermediateStructure.getClusters().values()) {
			
			Double progressStep = 89.0 / intermediateStructure.getNrClusters() / cluster.size();
			Set<IntermediateGroup> clusterReportGroups = new HashSet<IntermediateGroup>(cluster.size());
			
			// put every group with direct accessions into the report map map
			for (IntermediateGroup group : cluster) {
				
				
				logger.debug("proteins: " + group.getProteins()
						+"\n\thasPeptides " + groupHasReportPeptides(group, groupIdToReportPeptides));
				
				
				if (((group.getProteins() != null) && (group.getProteins().size() > 0)) &&
						groupHasReportPeptides(group, groupIdToReportPeptides)) {
					// report this group
					clusterReportGroups.add(group);
					
					// get the peptides of this group
					Set<IntermediatePeptide> allPeptidesSet =new HashSet<IntermediatePeptide>();
					groupsAllPeptides.put(group.getID(), allPeptidesSet);
					
					// add the direct peptides
					if (groupIdToReportPeptides.containsKey(group.getID())) {
						for (IntermediatePeptide peptide : groupIdToReportPeptides.get(group.getID())) {
							allPeptidesSet.add(peptide);
						}
					}
					
					// add childrens' peptides
					for (IntermediateGroup pepGroup : group.getAllPeptideChildren()) {
						if (groupIdToReportPeptides.containsKey(pepGroup.getID())) {
							for (IntermediatePeptide peptide : groupIdToReportPeptides.get(pepGroup.getID())) {
								allPeptidesSet.add(peptide);
							}
						}
					}
				}
				
				progress += progressStep;
				logger.debug("progress (step):  " + progress);
			}
			
			// check for sameSets (if there were active filters)
			if (/*(filters != null ) && (filters.size() > 0)*/ true) {
				
				sameSets = new HashMap<Integer, Set<IntermediateGroup>>(groupsAllPeptides.size());
				Set<Integer> newReportGroupIDs = new HashSet<Integer>(clusterReportGroups.size());
				
				for (Map.Entry<Integer, Set<IntermediatePeptide>> gIt : groupsAllPeptides.entrySet()) {
					// every group gets a sameSet
					Set<IntermediateGroup> sameSet = sameSets.get(gIt.getKey()); 
					if (sameSet == null) {
						sameSet = new HashSet<IntermediateGroup>();
						sameSets.put(gIt.getKey(), sameSet);
					}
					
					// check against the other report groups
					for (IntermediateGroup checkGroup : clusterReportGroups) {
						if (gIt.getKey() == checkGroup.getID()) {
							// don't check against self
							logger.debug("don't check " + gIt.getKey() + " with " + checkGroup.getID());
							continue;
						}
						
						if (gIt.getValue().equals(groupsAllPeptides.get(checkGroup.getID()))) {
							// ReportPeptides are the same in checkSet and grIt
							sameSet.add(checkGroup);
							
							// if checkID's group had a sameSet before, merge the sameSets
							Set<IntermediateGroup> checkSameSet = sameSets.get(checkGroup.getID());
							if (checkSameSet != null) {
								sameSet.addAll(checkSameSet);
							}
							sameSets.put(checkGroup.getID(), sameSet);
						}
					}
					
					
					// check, if any of the sameSet is already in the newReportGroups 
					boolean anySameInReportGroups = false;
					
					for (IntermediateGroup sameGroup : sameSet) {
						if (newReportGroupIDs.contains(sameGroup.getID())) {
							anySameInReportGroups = true;
							break;
						}
					}
					
					if (!anySameInReportGroups) {
						// no sameGroup in reportGroups yet, put this one in
						newReportGroupIDs.add(gIt.getKey());
					}
				}
				
				Set<IntermediateGroup> newReportGroups = new HashSet<IntermediateGroup>(newReportGroupIDs.size());
				for (IntermediateGroup group : clusterReportGroups) {
					if (newReportGroupIDs.contains(group.getID())) {
						newReportGroups.add(group);
					}
				}
				clusterReportGroups = newReportGroups;
			}
			
			
			// now create the proteins from the groups, which are in clusterReportGroups
			
			for (IntermediateGroup group : clusterReportGroups) {
				Set<Peptide> peptides = new HashSet<Peptide>();
				List<Protein> proteins = new ArrayList<Protein>();
				
				logger.debug("group " + group.getID());
				
				for (IntermediatePeptide interPeptide : groupsAllPeptides.get(group.getID())) {
					for (PeptideEvidence pepEvidence : interPeptide.getPeptideEvidences()) {
						for (SpectrumIdentification specId : interPeptide.getPeptideSpectrumMatches()) {
							peptides.add(new Peptide(pepEvidence, specId));
						}
					}
					
					logger.debug("\tpep " + interPeptide.getSequence());
				}

				Set<DBSequence> dbSequences = new HashSet<DBSequence>();
				for (IntermediateProtein interProt : group.getProteins()) {
					dbSequences.add(interProt.getRepresentative());
				}
				
				if (sameSets != null) {
					for (IntermediateGroup sameGroup : sameSets.get(group.getID())) {
						if (sameGroup.getID() == group.getID()) {
							logger.debug("don't add sequences " + group.getID() + " with " + sameGroup.getID());
							continue;
						}
						
						for (IntermediateProtein interProt : sameGroup.getProteins()) {
							dbSequences.add(interProt.getRepresentative());
						}
					}
				}
				
				
				for (DBSequence dbSeq : dbSequences) {
					
					String protID = "PDH_" + group.getID() + "_" + dbSeq.getAccession();
					Score score = null;
					double sequenceCoverage = -1;
					
					Protein protein = new Protein(protID,
							dbSeq.getAccession(),
							dbSeq,
							true /*passThreshold*/,
							new ArrayList<Peptide>(peptides),
							score,
							-1 /*threshold*/,
							sequenceCoverage,
							null /*gel*/);
					
					proteins.add(protein);
				}
				
				ProteinGroup proteinGroup = new ProteinGroup("PAG_" + group.getID(),
						"PAG_" + group.getID(),
						proteins);
				
				proteinGroups.add(proteinGroup);
			}
			
			progress += 10.0 / intermediateStructure.getNrClusters();
			logger.debug("progress cluster: " + progress);
		}
		
		logger.info("calculateInference done.");
		progress = 100.0;
		logger.debug("progress (done):  " + progress);
		return proteinGroups;
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	
	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public Long getProgressValue() {
		return progress.longValue();
	}
}