package uk.ac.ebi.pride.data.core;

/**
 * Description of the sample used to generate the dataset.
 * <p/>
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 15:50:55
 */
public class Sample extends ParamGroup {
    /**
     * identifier for the sample
     */
    private String id = null;
    /**
     * name for the sample
     */
    private String name = null;

    /**
     * Constructor
     *
     * @param id     required.
     * @param name   optional.
     * @param params optional.
     */
    public Sample(String id,
                  String name,
                  ParamGroup params) {
        super(params);
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
