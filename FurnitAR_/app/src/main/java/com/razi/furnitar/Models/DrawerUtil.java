package com.razi.furnitar.Models;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.razi.furnitar.Activities.CartActivity;
import com.razi.furnitar.Activities.ItemDetailActivity;
import com.razi.furnitar.Activities.MainActivity;
import com.razi.furnitar.R;
import com.razi.furnitar.Common;
import com.razi.furnitar.Utils.UserPreference;


public class DrawerUtil {

    public static void getDrawer(final Activity activity, Toolbar toolbar) {
        //if you want to update the items at a later time it is recommended to keep it in a variable

        PrimaryDrawerItem products = new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.products).withIcon(R.drawable.ic_subject_black_24dp);
        SecondaryDrawerItem show_cart = new SecondaryDrawerItem()
                .withIdentifier(2).withName(R.string.show_cart).withIcon(R.drawable.ic_shopping_cart_black_24dp);


        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(3)
                .withName(R.string.log_out).withIcon(R.drawable.ic_exit_to_app_black_24dp);

        String email = UserPreference.getInstance().get("user_email", "");
// Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.color.overlaybackground)
                .addProfiles(
                        new ProfileDrawerItem().withName(email).withEmail(email)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .withTranslucentNavigationBar(true)
                .addDrawerItems(
                        products,
                        new DividerDrawerItem(),
                        show_cart,
                        logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 1 && !(activity instanceof MainActivity)) {
                            Intent intent = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(intent);
                        } else if (drawerItem.getIdentifier() == 2) {
                            Intent intent = new Intent(activity, CartActivity.class);
                            view.getContext().startActivity(intent);
                        } else if (drawerItem.getIdentifier() == 3) {
                            Log.d(activity.getLocalClassName(), "Activity_name");
                            if (activity.getLocalClassName().equals("Activities.MainActivity"))
                                MainActivity.signOut();
                            else if (activity.getLocalClassName().equals("Activities.ItemDetailActivity"))
                                ItemDetailActivity.signOut();
                            else if (activity.getLocalClassName().equals("Activities.CartActivity"))
                                CartActivity.signOut();
                            else {
                                String name = activity.getLocalClassName();
                                Log.d(name, "Activity_name");
                            }
                        }
                        return true;
                    }
                })
                .build();
    }
}

