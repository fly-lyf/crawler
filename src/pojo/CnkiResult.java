package pojo;

/**
 * Created by Administrator on 2015/9/4.
 */
public class CnkiResult {
    private String count;
    private String[] type;
    private String[] url;
    private Integer[] citation;
    private Integer selfCitation;
    private Integer selfAddCitation;
    public String aa;

    public Integer[] getCitation() {
        return citation;
    }

    public void setCitation(Integer[] citation) {
        this.citation = citation;
    }

    public Integer getSelfCitation() {
        return selfCitation;
    }

    public void setSelfCitation(Integer selfCitation) {
        this.selfCitation = selfCitation;
    }

    public Integer getSelfAddCitation() {
        return selfAddCitation;
    }

    public void setSelfAddCitation(Integer selfAddCitation) {
        this.selfAddCitation = selfAddCitation;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String[] getUrl() {
        return url;
    }

    public void setUrl(String[] url) {
        this.url = url;
    }

}
