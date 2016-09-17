package pojo;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/4.
 */
public class CnkiResult {
    private Integer count;
    private HashMap<String, Integer> type;
    private HashMap<String, Integer> excludedType;
    private String[] url;
    private HashMap<String, Integer> citation;
    private Integer selfCitation;
    private Integer selfInstituteCitation;

    public HashMap<String, Integer> getCitation() {
        return citation;
    }

    public void setCitation(HashMap<String, Integer> citation) {
        this.citation = citation;
    }

    public Integer getSelfCitation() {
        return selfCitation;
    }

    public void setSelfCitation(Integer selfCitation) {
        this.selfCitation = selfCitation;
    }

    public Integer getSelfInstituteCitation() {
        return selfInstituteCitation;
    }

    public void setSelfInstituteCitation(Integer selfInstituteCitation) {
        this.selfInstituteCitation = selfInstituteCitation;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public HashMap<String, Integer> getType() {
        return type;
    }

    public void setType(HashMap<String, Integer> type) {
        this.type = type;
    }

    public String[] getUrl() {
        return url;
    }

    public void setUrl(String[] url) {
        this.url = url;
    }

    public HashMap<String, Integer> getExcludedType() {
        return excludedType;
    }

    public void setExcludedType(HashMap<String, Integer> excludedType) {
        this.excludedType = excludedType;
    }

    @Override
    public String toString() {
        String str = "总结果数="+this.getCount()+"\n自引数量="+this.getSelfCitation()+"\n机构自引数量="+this.getSelfInstituteCitation()+"\n";
        for (Iterator<Map.Entry<String, Integer>> iterator = this.type.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Integer> entry = iterator.next();
            str += "论文类型："+entry.getKey()+"   被引次数："+entry.getValue()+"\n";
        }
        for (Iterator<Map.Entry<String, Integer>> iterator = this.citation.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Integer> entry = iterator.next();
            str += "年份："+entry.getKey()+"   被引次数："+entry.getValue()+"\n";
        }
        return str;
    }
}
