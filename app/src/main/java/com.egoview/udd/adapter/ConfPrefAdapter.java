package com.egoview.udd.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoview.udd.R;
import com.egoview.udd.clases.categorias;
import com.egoview.udd.procesos.Porcentual;

import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;

/**
 * Created by Serial Desarrollo on 06-01-2016.
 */
public class ConfPrefAdapter extends RecyclerView.Adapter<ConfPrefAdapter.ViewHolderRecyclerView>
{
    private Activity activity;
    private LayoutInflater layoutInflater;
    private ArrayList<categorias> lista;
    private ArrayList<categorias> lista_subcat;

    public ConfPrefAdapter(Activity activity, ArrayList<categorias> lista, ArrayList<categorias> subcat, Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.activity = activity;
        this.lista_subcat = subcat;
        ArrayList<categorias> lista_cat_subcat = new ArrayList<categorias>();
        for(int i=0;i<lista.size();i++){
            lista_cat_subcat.add(lista.get(i));
            for(int j=0;j<subcat.size();j++){
                if(lista.get(i).getId_categorias()==lista_subcat.get(j).getId_categorias()){
                    lista_cat_subcat.add(lista_subcat.get(j));
                }
            }
        }
        this.lista=lista_cat_subcat;
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public ViewHolderRecyclerView onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolderRecyclerView viewHolderRecyclerView;
        if(viewType==0){
            View view = layoutInflater.inflate(R.layout.list_preferencias, parent, false);
            viewHolderRecyclerView = new ViewHolderRecyclerView(view, activity, "Especial");
        }else {
            View view = layoutInflater.inflate(R.layout.list_preferencias, parent, false);
            viewHolderRecyclerView = new ViewHolderRecyclerView(view, activity);
        }
        return viewHolderRecyclerView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getItemViewType (int position) {
        //Some logic to know which type will come next;
        return (position==0 || position==11) ? 0 : 1;
    }

    public ArrayList<categorias> getLista(){
        ArrayList<categorias> retornar = new ArrayList<>();
        for (int i=0;i<lista.size();i++){
            if(lista.get(i).getEstado()){
                retornar.add(lista.get(i));
            }
        }
        return retornar;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final ViewHolderRecyclerView holder, final int position)
    {
        holder.setIsRecyclable(false);
        holder.imagen.setBackgroundResource(activity.getResources().getIdentifier("pref" + lista.get(position).getId_categorias(), "drawable", activity.getPackageName()));
        holder.texto.setText(lista.get(position).getDescripcion_campania());
        holder.switchCompat.setChecked(lista.get(position).getEstado());

        if (holder.switchCompat.isChecked()) {
            holder.switchCompat.setThumbColorRes(R.color.azulclaro_college);
        } else {
            holder.switchCompat.setThumbColorRes(R.color.blanco);
        }
        holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.switchCompat.isChecked()) {
                    holder.switchCompat.setThumbColorRes(R.color.azulclaro_college);
                } else {
                    holder.switchCompat.setThumbColorRes(R.color.blanco);
                }
                lista.get(position).setEstado(holder.switchCompat.isChecked());
            }
        });
    }

    static class ViewHolderRecyclerView extends RecyclerView.ViewHolder{

        private ImageView imagen;
        private TextView texto;
        private SwitchButton switchCompat;


        public ViewHolderRecyclerView(View itemView, Activity activity){
            super(itemView);

            imagen = (ImageView) itemView.findViewById(R.id.img_pref);
            texto = (TextView) itemView.findViewById(R.id.txt_pref);
            switchCompat = (SwitchButton) itemView.findViewById(R.id.switch_pref);
            int width = new Porcentual().getScreenWidth(activity);
            int height= new Porcentual().getScreenHeight(activity);
            double sizeL = new Porcentual().getScreenInches(activity);
            Typeface type = Typeface.createFromAsset(activity.getAssets(), "roboto_regular.ttf");


            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imagen.getLayoutParams();
            layoutParams.width = (int) (width * 0.1);
            layoutParams.height = layoutParams.width;
            layoutParams.setMargins((int) (width * 0.01), (int) (height * 0.01), (int) (width * 0.01), (int) (height * 0.025));
            imagen.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)switchCompat.getLayoutParams();
            params.setMarginEnd((int) (width * 0.05));
            switchCompat.setThumbSize((int) (width * 0.07), (int) (width * 0.07));
            switchCompat.setThumbRadius((float) (width * 0.035));
            switchCompat.setBackRadius((int) (width * 0.1));

            texto.setTextSize((float) (sizeL * 2.5));
            texto.setTypeface(type);
        }
        public ViewHolderRecyclerView(View itemView, Activity activity, String string){
            super(itemView);

            imagen = (ImageView) itemView.findViewById(R.id.img_pref);
            texto = (TextView) itemView.findViewById(R.id.txt_pref);
            switchCompat = (SwitchButton) itemView.findViewById(R.id.switch_pref);
            int width = new Porcentual().getScreenWidth(activity);
            int height= new Porcentual().getScreenHeight(activity);
            double sizeL = new Porcentual().getScreenInches(activity);
            Typeface bold= Typeface.createFromAsset(activity.getAssets(), "roboto_bold.ttf");

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imagen.getLayoutParams();
            layoutParams.width = (int) (width * 0.1);
            layoutParams.height = layoutParams.width;
            layoutParams.setMargins((int) (width * 0.01), (int) (height * 0.01), (int) (width * 0.01), (int) (height * 0.025));
            imagen.setLayoutParams(layoutParams);

            imagen.setVisibility(View.INVISIBLE);
            switchCompat.setVisibility(View.GONE);
            texto.setTextColor(activity.getResources().getColor(R.color.azul_cupon));

            texto.setTextSize((float) (sizeL * 3));
            texto.setTypeface(bold);
        }
    }
}