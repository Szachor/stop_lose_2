package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.xstore2.XtbAsyncService
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val login = "12263751"
        val password = "xoh26561"

        val x: XtbAsyncService = XtbAsyncService(login, password);

        println("<---------------------XtbAsyncService--------------------->");
        println("<---------------------OUTPUT--------------------->");
        val result = x.allSymbolsAsync.get()
        println(result);
        val result2 = x.getProfitCalculationAsync(1.3000.toFloat(), 0, 1.23333.toFloat(),"EURPLN", 1.0.toFloat()).get()
        println(result2);
        val result3 = x.getSymbolAsync("EURPLN").get()
        println(result3);

        x.subscribeGetKeepAlive();
        x.subscribeGetTicketPrice("USDPLN")
        x.subscribeGetTicketPrice("EURUSD")
        x.subscribeGetTicketPrice("EURPLN")
        val queueResponses =  x.subscriptionResponses
        while(true){
            val mainResponse =
                """
                <---------------------------------------------------------------------->
                <---------------------------Logs Taken From Queue----------------------------------->
                This is response from Queue
                ${queueResponses.take()}
                <---------------------------Logs Taken From Queue-------------------------------->
                <---------------------------------------------------------------------->
                """.trimIndent()

            println(mainResponse);
        }


        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}