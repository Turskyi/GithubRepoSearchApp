package ua.turskyi.datafrominternet;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;

import ua.turskyi.datafrominternet.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    EditText mSearchBoxEditText;

    TextView mUrlDisplayTextView;
    TextView mSearchResultsTextView;

    TextView mErrorMessageDisplay;

    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchBoxEditText = findViewById(R.id.etSearchBox);

        mUrlDisplayTextView = findViewById(R.id.tvUrlDisplay);
        mSearchResultsTextView = findViewById(R.id.tvGitHubSearchResultsJson);

        mErrorMessageDisplay = findViewById(R.id.tvErrorMessageDisplay);

        mLoadingIndicator = findViewById(R.id.pbLoadingIndicator);
    }

    void makeGithubSearchQuery() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    String githubQuery = mSearchBoxEditText.getText().toString();
                    URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
                    mUrlDisplayTextView.setText(githubSearchUrl.toString());
                    String githubSearchResults;
                    try {
                        githubSearchResults =  NetworkUtils.getResponseFromHttpUrl(githubSearchUrl);
                        mSearchResultsTextView.setText(githubSearchResults);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    new GithubQueryTask().execute(githubSearchUrl);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void showJsonDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("StaticFieldLeak")
    private class GithubQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
           URL searchUrl = params[0];
           String githubSearchResults = null;
           try {
               githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
           } catch (IOException e) {
               e.printStackTrace();
           }
           return githubSearchResults;
        }

        @Override
        protected void onPostExecute(String githubSearchResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (githubSearchResults != null && !githubSearchResults.equals("")) {
                showJsonDataView();
                mSearchResultsTextView.setText(githubSearchResults);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if (menuItemThatWasSelected == R.id.actionSearch) {
            makeGithubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
