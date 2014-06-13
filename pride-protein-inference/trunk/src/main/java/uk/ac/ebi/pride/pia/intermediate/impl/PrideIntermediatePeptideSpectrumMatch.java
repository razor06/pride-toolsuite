package uk.ac.ebi.pride.pia.intermediate.impl;

import java.util.List;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.PeptideEvidence;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.term.CvTermReference;


/**
 * Representation of a peptide spectrum match in the intermediate structure.
 * 
 * @author julian
 *
 */
public class PrideIntermediatePeptideSpectrumMatch implements IntermediatePeptideSpectrumMatch {
	
	/** a unique ID (cached on the first accession) */
	private Comparable id;
	
	/** the used PRIDE dataAccessController */
	private DataAccessController controller;
	
	/** the protein ID for accession by a PRIDE dataAccessController */
	private Comparable proteinID;
	
	/** the peptide ID for accession by a PRIDE dataAccessController */
	private Comparable peptideID;
	
	/** the decoy status, when overriding the original status */
	private Boolean isDecoy;
	
	/** the calculated FDR value */
	private Double fdrValue;
	
	/** the calculated q-value */
	private Double qValue;
	
	
	public PrideIntermediatePeptideSpectrumMatch(DataAccessController controller,
			Comparable proteinID, Comparable peptideID) {
		this.controller = controller;
		this.proteinID = proteinID;
		this.peptideID = peptideID;
		this.isDecoy = null;
		this.fdrValue = null;
		this.qValue = null;
	}
	
	
	@Override
	public Comparable getID() {
		if (id == null) {
			id = controller.getUid() + ":" + getSpectrumIdentification().getId();
		}
		return id;
	}
	
	
	@Override
	public Double getScore(String scoreAccession) {
		CvTermReference cvTermRef = CvTermReference.getCvRefByAccession(scoreAccession);
		if (cvTermRef != null) {
			List<Number> scores = 
					getSpectrumIdentification().getScore().getScores(cvTermRef);
			
			if (scores.size() > 0) {
				return scores.get(0).doubleValue();
			}
		}
		
		return null;
	}
	
	
	@Override
	public Boolean getIsDecoy() {
		if (isDecoy != null) {
			return isDecoy;
		} else {
			boolean decoy = true;
			
			for (PeptideEvidence pepEvidence : getSpectrumIdentification().getPeptideEvidenceList()) {
				decoy &= pepEvidence.isDecoy();
				if (!decoy) {
					// as soon as it is no more a decoy, return false
					return false;
				}
			}
			
			return decoy;
		}
	}
	
	
	@Override
	public void setIsDecoy(Boolean isdecoy) {
		this.isDecoy = isDecoy;
	}
	
	
	@Override
	public SpectrumIdentification getSpectrumIdentification() {
		return controller.getPeptideByIndex(proteinID, peptideID).getSpectrumIdentification();
	}
	
	
	@Override
	public void setFDR(Double fdr) {
		this.fdrValue = fdr;
	}
	
	
	@Override
	public Double getFDR() {
		return fdrValue;
	}
	
	
	@Override
	public void setQValue(Double value) {
		this.qValue = value;
	}
	
	
	@Override
	public Double getQValue() {
		return qValue;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || !(obj instanceof IntermediatePeptideSpectrumMatch)) return false;
		
        IntermediatePeptideSpectrumMatch psm = (IntermediatePeptideSpectrumMatch)obj;
		
        if (!getID().equals(psm.getID())) return false;
		return getSpectrumIdentification().equals(psm.getSpectrumIdentification());
	}
	
	
	@Override
	public int hashCode() {
        int result = getID().hashCode();
        result = 31 * result + peptideID.hashCode();
        result = 31 * result + proteinID.hashCode();
        return result;
	}
}
