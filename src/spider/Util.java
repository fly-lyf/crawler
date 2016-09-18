package spider;

import pojo.SearchResult;

/**
 * Created by Administrator on 2016/9/15.
 */
public class Util {
    //查询条件格式化——处理空格等各种标点符号
    public String[] formatSearchResult(SearchResult searchResult) {
        //去空格
        String title = searchResult.getTitle();
        String author = searchResult.getAuthor();
        String spareTitle = searchResult.getSpareTitle();
        String spareAuthor = searchResult.getSpareAuthor();
        while (title.indexOf(" ") != -1) {
            title = title.substring(0, title.indexOf(" ")) + title.substring(title.indexOf(" ") + 1, title.length());
        }
        //引号改英文
        title = title.replace("“", "\"");
        title = title.replace("”", "\"");
        //截取冒号，破折号之前的书名
        if (title.indexOf(":") != -1) {
            title = title.substring(0, title.indexOf(":"));
        }else if (title.indexOf("•") != -1) {
            title = title.substring(0, title.indexOf("•"));
        }else if (title.indexOf("——") != -1) {
            title = title.substring(0, title.indexOf("——"));
        }else if (title.indexOf("--") != -1) {
            title = title.substring(0, title.indexOf("--"));
        }else {
            if (title.indexOf("·") != -1) {
                title = title.substring(0, title.indexOf("·"));
            }
        }

        //作者截断
        if (author.indexOf("•") != -1) {
            author = author.substring(0, author.indexOf("•"));
        } else if (author.indexOf("·") != -1) {
            author = author.substring(0, author.indexOf("·"));
        }
        //备用书名处理
        if (spareTitle != null) {
            while (spareTitle.indexOf(" ") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf(" ")) + spareTitle.substring(spareTitle.indexOf(" ") + 1, spareTitle.length());
            }
            //引号改英文
            spareTitle = spareTitle.replace("“", "\"");
            spareTitle = spareTitle.replace("”", "\"");
            //截取冒号，破折号之前的书名
            if (spareTitle.indexOf(":") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf(":"));
            } else
            if (spareTitle.indexOf("•") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("•"));
            } else
            if (spareTitle.indexOf("——") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("——"));
            } else
            if (spareTitle.indexOf("--") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("--"));
            }else{

            }if (spareTitle.indexOf("·") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("·"));
            } else if (spareTitle.indexOf("、") != -1) {
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("、"));
            }
        }
        if (spareAuthor != null) {
            //备用作者截断
            if (spareAuthor.indexOf("•") != -1) {
                spareAuthor = spareAuthor.substring(0, spareAuthor.indexOf("•"));
            } else if (spareAuthor.indexOf("·") != -1) {
                spareAuthor = spareAuthor.substring(0, spareAuthor.indexOf("·"));
            }
        }
        String[] results = new String[]{title, author, spareTitle, spareAuthor};
        return results;
    }

    //查询结果书名格式化
    public String formatTitleString(String title){
        while (title.indexOf(" ") != -1) {
            title = title.substring(0, title.indexOf(" ")) + title.substring(title.indexOf(" ") + 1, title.length());
        }
        title = title.replace("“", "\"");
        title = title.replace("”", "\"");
        title = title.replace("（", "(");
        title = title.replace("）", ")");
        title = title.replace("：", ":");
        return title;
    }

    //自动生成部分备用书名
    public String createSpareTile(String title, String spareTitle){
        if(spareTitle != null && !spareTitle.equals("")){
            return spareTitle;
        }else {
            //书名号
            if(title.contains("《")){
                spareTitle = title;
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("《"))+spareTitle.substring(spareTitle.indexOf("《")+1, spareTitle.length());
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("》"))+spareTitle.substring(spareTitle.indexOf("》")+1, spareTitle.length());
                return spareTitle;
            }
            //引号
            if(title.contains("\"")){
                spareTitle = title;
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("\""))+spareTitle.substring(spareTitle.indexOf("\"")+1, spareTitle.length());
                spareTitle = spareTitle.substring(0, spareTitle.indexOf("\""))+spareTitle.substring(spareTitle.indexOf("\"")+1, spareTitle.length());
                return spareTitle;
            }
            //顿号
            if(title.contains("、")){
                spareTitle = title;
                while(spareTitle.contains("、")){
                    spareTitle = spareTitle.substring(0, spareTitle.indexOf("、"))+spareTitle.substring(spareTitle.indexOf("、")+1, spareTitle.length());
                }
                return spareTitle;
            }
        }
        return spareTitle;
    }
}
