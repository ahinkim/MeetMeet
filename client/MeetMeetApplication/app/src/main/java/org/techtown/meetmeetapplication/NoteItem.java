package org.techtown.meetmeetapplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteItem {
    String _id;//서버 아이디
    String contents;
    String createDateStr;
    public NoteItem(String _id,String contents, String createDateStr){
        this._id=_id;
        this.contents=contents;
        this.createDateStr=createDateStr;
    }
    public String getId(){return _id;}
    public void setId(String _id){this._id=_id;}

    public String getContents(){
        return contents;
    }
    public void setContents(String contents){
        this.contents=contents;
    }

    public String getCreateDateStr(){

        return createDateStr;
    }
    public void setCreateDateStr(String date){
        this.createDateStr=createDateStr;
    }
}
