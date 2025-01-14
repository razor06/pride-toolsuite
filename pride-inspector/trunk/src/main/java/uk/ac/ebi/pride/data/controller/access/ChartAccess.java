package uk.ac.ebi.pride.data.controller.access;

import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.component.chart.PrideChartManager;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: fabregat
 * Date: 05-oct-2010
 * Time: 10:58:58
 */
public interface ChartAccess {
    public List<PrideChartManager> getChartData() throws DataAccessException;
}
