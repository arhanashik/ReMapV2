package com.bodytel.remapv2.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/*
 * ****************************************************************************
 * * Copyright © 2018 W3 Engineers Ltd., All rights reserved.
 * *
 * * Created by:
 * * Name : Azizul Islam
 * * Date : 10/13/17
 * * Email : azizul@w3engineers.com
 * *
 * * Purpose: Child view holder class inherit this class
 * *
 * * Last Edited by : SUDIPTA KUMAR PAIK on 03/16/18.
 * * History:
 * * 1:
 * * 2:
 * *
 * * Last Reviewed by : SUDIPTA KUMAR PAIK on 03/16/18.
 * ****************************************************************************
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {

    public BaseViewHolder(View view) {
        super(view);
    }
    /*
     * Child class have to implement this method.
     * */
    public abstract void bind(T item);

    /**
     * To set click listener on any view, You can pass multiple view at a time
     *
     * @param views View as params
     * @return void
     */
    protected void setClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }
}