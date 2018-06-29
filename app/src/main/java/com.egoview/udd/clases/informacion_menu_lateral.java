package com.egoview.udd.clases;

/**
 * Created by Serial on 27-10-2015.
 */
public class informacion_menu_lateral {
    private int iconId;
    private String title;
    private int id;



    public informacion_menu_lateral(){
    }

    public void setIconId(int _iconId) {
        iconId = _iconId;
    }

    public void setTitle(String _title){
        title=_title;
    }

    public int getIconId(){
        return iconId;
    }

    public String getTitle(){
        return title;
    }

    public int getId() {return id;}

    public void  setId(int _id){
        id=_id;
    }
}
