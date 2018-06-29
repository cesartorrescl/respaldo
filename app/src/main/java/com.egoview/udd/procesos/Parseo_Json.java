package com.egoview.udd.procesos;

import com.egoview.udd.clases.campanias;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.clases.subcategorias;
import com.egoview.udd.clases.comunas;
import com.egoview.udd.clases.htmlCamp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Serial on 06/10/2015 modified 30/11/2015.*/
public class Parseo_Json{

    //Metodo que parsea la respuesta Json del mobile services Azure
    public static ArrayList<categorias> parseoJsonCategorypersonalizado(String Respuesta) {
        ArrayList<categorias> list_categorias;
        list_categorias = new ArrayList<categorias>();
        JSONArray lista_categorias_json = null;
        try
        {
            lista_categorias_json = new JSONArray(Respuesta);
            Pattern pat = Pattern.compile(".*Exception.*");
            Matcher mat = pat.matcher(Respuesta);

            if (mat.matches())
            {
            }
            else
            {
                for (int i = 0; i < lista_categorias_json.length(); i++)
                {
                    JSONObject categorias_object = lista_categorias_json.getJSONObject(i);// para capturar un elemento;
                    categorias categorias_user = new categorias();
                    categorias_user.setId(categorias_object.optInt("$id"));
                    categorias_user.setId_categorias(categorias_object.optInt("id_tipoCampania"));
                    categorias_user.setDescripcion_campania(categorias_object.optString("desc_tipoCamp"));
                    list_categorias.add(categorias_user);

                }
            }
            return list_categorias;
        } catch (Exception e) {
            return list_categorias;
       }

    }//end method

    public static ArrayList<subcategorias> parseoJsonSubCategory(String Respuesta) {
        ArrayList<subcategorias> list_subcategorias;
        list_subcategorias = new ArrayList<subcategorias>();
        JSONArray lista_subcategorias_json = null;
        try
        {
            lista_subcategorias_json = new JSONArray(Respuesta);
            Pattern pat = Pattern.compile(".*Exception.*");
            Matcher mat = pat.matcher(Respuesta);

            if (mat.matches())
            {
            }
            else
            {
                for (int i = 0; i < lista_subcategorias_json.length(); i++)
                {
                    JSONObject categorias_object = lista_subcategorias_json.getJSONObject(i);// para capturar un elemento;
                    subcategorias subcategorias_user = new subcategorias();
                    subcategorias_user.setId(categorias_object.optInt("$id"));
                    subcategorias_user.setDescripcion_campania(categorias_object.optString("desc_subCategoria"));
                    list_subcategorias.add(subcategorias_user);

                }
            }
            return list_subcategorias;
        } catch (Exception e) {
            return list_subcategorias;
        }

    }//end method

    public static ArrayList<comunas> parseoJsonComunas(String Respuesta) {
        ArrayList<comunas> list_comunas;
        list_comunas = new ArrayList<>();
        //String result =categoria01.getRespuestaJson();
        //boolean estado = usuario01.getEstado();
        JSONArray lista_comunas_json = null;
        try
        {
            lista_comunas_json = new JSONArray(Respuesta);
            Pattern pat = Pattern.compile(".*Exception.*");
            Matcher mat = pat.matcher(Respuesta);

            if (mat.matches())
            {
            }
            else
            {
                for (int i = 0; i < lista_comunas_json.length(); i++)
                {
                    JSONObject comuna_object = lista_comunas_json.getJSONObject(i);// para capturar un elemento;
                    comunas comuna01 = new comunas();
                    comuna01.setmId(comuna_object.optString("$id"));
                    comuna01.setmIdcomuna(comuna_object.optInt("idComuna"));
                    comuna01.setmDescomuna(comuna_object.optString("descComuna"));
                    comuna01.setmIdciudad(comuna_object.optInt("idCiudad"));
                    comuna01.setmDesregion(comuna_object.optString("descRegion"));
                    list_comunas.add(comuna01);
                }
            }
            return list_comunas;
        } catch (JSONException e) {
            return list_comunas;
        }
    }

    //Metodo que parsea la respuesta Json del mobile services Azure
    public static ArrayList<categorias> parseoJsonCategorygeneric(String Respuesta) {
        ArrayList<categorias> list_categorias;
        list_categorias = new ArrayList<categorias>();
        //String result =categoria01.getRespuestaJson();
        //boolean estado = usuario01.getEstado();
        JSONArray lista_categorias_json = null;
        try
        {
            lista_categorias_json = new JSONArray(Respuesta);
            Pattern pat = Pattern.compile(".*Exception.*");
            Matcher mat = pat.matcher(Respuesta);

            if (mat.matches())
            {
            }
            else
            {
                for (int i = 0; i < lista_categorias_json.length(); i++)
                {
                    JSONObject categorias_object = lista_categorias_json.getJSONObject(i);// para capturar un elemento;
                    categorias categorias_user = new categorias();
                    categorias_user.setId(categorias_object.optInt("$id"));
                    categorias_user.setId_categorias(categorias_object.optInt("id_tipoCampania"));
                    categorias_user.setDescripcion_campania(categorias_object.optString("desc_tipoCamp"));
                    list_categorias.add(categorias_user);
                }
            }
            return list_categorias;
        } catch (Exception e) {
            return list_categorias;
        }
    }//end method

    public static ArrayList<categorias> parseoJsonCategorygenericPref(String Respuesta) {
        ArrayList<categorias> list_categorias;
        list_categorias = new ArrayList<categorias>();
        JSONArray lista_categorias_json = null;
        try
        {
            lista_categorias_json = new JSONArray(Respuesta);
            Pattern pat = Pattern.compile(".*Exception.*");
            Matcher mat = pat.matcher(Respuesta);

            if (mat.matches())
            {
            }
            else
            {
                for (int i = 0; i < lista_categorias_json.length(); i++)
                {
                    if(i==0){
                        categorias categorias_user = new categorias();
                        categorias_user.setId(998);
                        categorias_user.setId_categorias(998);
                        categorias_user.setDescripcion_campania("Categorías populares");
                        list_categorias.add(categorias_user);
                    }else{
                        if (i==10){
                            categorias categorias_user2 = new categorias();
                            categorias_user2.setId(997);
                            categorias_user2.setId_categorias(997);
                            categorias_user2.setDescripcion_campania("Otras categorías");
                            list_categorias.add(categorias_user2);
                        }
                    }
                    categorias categorias_user3 = new categorias();
                    JSONObject categorias_object = lista_categorias_json.getJSONObject(i);// para capturar un elemento;
                    categorias_user3.setId(categorias_object.optInt("$id"));
                    categorias_user3.setId_categorias(categorias_object.optInt("id_tipoCampania"));
                    categorias_user3.setDescripcion_campania(categorias_object.optString("desc_tipoCamp"));
                    list_categorias.add(categorias_user3);
                }
            }
            return list_categorias;
        } catch (Exception e) {
            return list_categorias;
        }
    }//end method


    //Metodo parsea respuesta del servicio. Crear clase Campania
    public static ArrayList<campanias> parseoJsonCampain(String Respuesta)
    {
        String result = Respuesta;
        ArrayList<campanias> campaniasList= new ArrayList<campanias>();
        try
        {
            Pattern pat = Pattern.compile(".*Exception.*");
            Matcher mat = pat.matcher(result);

            if(mat.matches())
            {
                return null;
            }
            else
            {
                //Aqui completa la clase campania con htmlCamp y imagenCamp
                JSONArray jsonArray = new JSONArray(result);
                for(int i=0; i < jsonArray.length(); i++) {
                    campanias campania01 = new campanias();
                    campania01.setEstado(true);
                    campania01.setMensajePersonalizado("sin errores");
                    campania01.setPersonalizado(false);

                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    campania01.setIdCampania(jsonObject.optString("id_campania"));
                    campania01.setId_tipoCampania(jsonObject.optString("idCategoria"));
                    campania01.setId_tipoCampania(jsonObject.optString("idCategoria"));
                    campania01.setNombre_camp(jsonObject.optString("nombre_camp"));
                    campania01.setDesc_campania(jsonObject.optString("desc_campania"));
                    campania01.setMdistancia(jsonObject.optDouble("distancia"));
                    campania01.setCondiciones_uso(jsonObject.optString("condiciones_uso"));
                    campania01.setUrl_sitio_externo(jsonObject.optString("url_sitio_externo"));
                    campania01.setRuta_imagen(jsonObject.optString("ruta_imagen"));
                    campania01.setFav(jsonObject.optBoolean("isFav"));
                    campania01.setMG(jsonObject.optBoolean("leGusta"));
                    campania01.setNMG(jsonObject.optBoolean("noLeGusta"));
                    campania01.setCantMG(jsonObject.optInt("totalMeGusta"));
                    campania01.setCantNMG(jsonObject.optInt("totalNoMeGusta"));
                    campania01.setVisto(jsonObject.optBoolean("isVisto"));
                    campania01.setNombre_empresa(jsonObject.optString("razonSocialEmp"));
                    campania01.setNombre_categoria(jsonObject.optString("razonSocialEmp"));
                    campania01.setIsEmpresa(jsonObject.optBoolean("isEmp"));
                    campania01.setisPagada(jsonObject.optBoolean("isPagado"));
                    campania01.setValoracion(jsonObject.optDouble("promValoracionEmp"));
                    campania01.setIdEmpresa(jsonObject.optLong("idEmpresa"));
                    campania01.setValoracionUser(jsonObject.optInt("miValoracion"));
                    htmlCamp html = new htmlCamp();
                    html.setCod_html(jsonObject.optString("cod_html"));
                    campania01.setHtmlCampArrayList(html);

                    campaniasList.add(campania01);
                }
            }
            return campaniasList;
        }
        catch(Exception e)
        {
            return null;
        }
    }//end method

    public static campanias parseoJsonCampainFinal(String Respuesta, campanias campania01) {
        JSONObject lista_sucursales_json = null;
        try
        {
            lista_sucursales_json = new JSONObject(Respuesta);
            Pattern pat = Pattern.compile(".*Exception.*");
            Matcher mat = pat.matcher(Respuesta);

            if (mat.matches())
            {
            }
            else
            {
                campania01.setInicio_camp(lista_sucursales_json.optString("inicio_camp"));
                campania01.setFin_camp(lista_sucursales_json.optString("fin_camp"));
                campania01.setUrl_sitio_externo(lista_sucursales_json.optString("url_sitio_externo"));
                campania01.setCondiciones_uso(lista_sucursales_json.optString("condiciones_uso"));
                campania01.setNombre_empresa(lista_sucursales_json.optString("razonSocialEmp"));
                htmlCamp html = new htmlCamp();
                html.setCod_html(lista_sucursales_json.optString("cod_html"));
                campania01.setHtmlCampArrayList(html);
            }
            return campania01;
        }
        catch (Exception e)
        {
            return campania01;
        }
    }
}
