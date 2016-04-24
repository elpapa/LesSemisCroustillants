package edu.imag.miage.lessemiscroustillants;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import edu.imag.miage.lessemiscroustillants.data.ArticleContract;
import edu.imag.miage.lessemiscroustillants.model.ArticleContent;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * An activity representing a list of Articles. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ArticleDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ArticleListActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(ArticleListActivity.this,AddArticleActivity.class);
                startActivity(addIntent);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        Menu menu = navigationView.getMenu();
        Cursor mCursor = MyApplication.getAppContext().getContentResolver().query(
                ArticleContract.StockEntry.CONTENT_URI,
                new String[]{ArticleContract.StockEntry.COLUMN_STOCK_NAME},
                null,null,null,null);

        while(mCursor.moveToNext()){
            menu.add(mCursor.getString(0));
        }
        mCursor.close();


        navigationView.setNavigationItemSelectedListener(this);

        View recyclerView = findViewById(R.id.article_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.article_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String name = item.getTitle().toString();

        if (id == R.id.add_stock) {
            Intent intent = new Intent(ArticleListActivity.this, AddStockActivity.class);
            startActivity(intent);
        } else if (id == R.id.dbm){
            Intent dbmanager = new Intent(ArticleListActivity.this,AndroidDatabaseManager.class);
            startActivity(dbmanager);
        } else {
            Intent addIntent = new Intent(ArticleListActivity.this, StockListProductActivity.class)
                    .putExtra("stock_name", name);
            startActivity(addIntent);
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
        } else if ( id == R.id.filter){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Filtrer les résultats", Toast.LENGTH_SHORT);
            toast.show();
        } else if(id == R.id.search_article){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Rechercher un produit", Toast.LENGTH_SHORT);
            toast.show();
        }
        return super.onOptionsItemSelected(item);



    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {


        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(ArticleContent.ITEMS));
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ArticleContent.ArticleItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<ArticleContent.ArticleItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.article_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);
            holder.mQuantiteView.setText(mValues.get(position).quantite);

            FetchImageArticleTask fetchImageArticleTask = new FetchImageArticleTask();
            fetchImageArticleTask.execute(mValues.get(position).url_image);
            Bitmap bitmap = null;
            try {
                bitmap = fetchImageArticleTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            holder.mImageView.setImageBitmap(bitmap);

            holder.mImageButtonAdd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int quantite = Integer.parseInt(mValues.get(position).quantite);
                    quantite ++;

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ArticleContract.ProductEntry.COLUMN_PRODUCT_QUANTITE,quantite);

                    int updated = getApplicationContext().getContentResolver().update(
                            ArticleContract.ProductEntry.CONTENT_URI,
                            contentValues,
                            ArticleContract.ProductEntry.TABLE_NAME + "." + ArticleContract.ProductEntry._ID + " = ?",
                            new String[]{mValues.get(position).id}
                    );

                    if(updated > 0){
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Ajout d\'un produit", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            });

            holder.mImageButtonDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Supprimer un produit", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ArticleDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ArticleDetailFragment fragment = new ArticleDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.article_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ArticleDetailActivity.class);
                        intent.putExtra(ArticleDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mQuantiteView;
            public final ImageView mImageView;
            public final ImageButton mImageButtonDelete;
            public final ImageButton mImageButtonAdd;
            public ArticleContent.ArticleItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mQuantiteView = (TextView) view.findViewById(R.id.quantite);
                mImageView = (ImageView) view.findViewById(R.id.image);
                mImageButtonDelete = (ImageButton) view.findViewById(R.id.delete_article);
                mImageButtonAdd = (ImageButton) view.findViewById(R.id.add_to_stock);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

}
