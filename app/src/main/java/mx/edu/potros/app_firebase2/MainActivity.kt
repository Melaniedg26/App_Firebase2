package mx.edu.potros.app_firebase2

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class MainActivity : AppCompatActivity() {
    private lateinit var txtid: EditText
    private lateinit var txtnom: EditText
    private lateinit var btnbus: Button
    private lateinit var btnmod: Button
    private lateinit var btnreg: Button
    private lateinit var btneli: Button
    private lateinit var lvDatos: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtid = findViewById(R.id.txtid)
        txtnom = findViewById(R.id.txtnom)
        btnbus = findViewById(R.id.btnbus)
        btnmod = findViewById(R.id.btnmod)
        btnreg = findViewById(R.id.btnreg)
        btneli = findViewById(R.id.btneli)
        lvDatos = findViewById(R.id.lvDatos)

        botonBuscar();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarLuchadores();

    }


    private fun botonBuscar() {
        btnbus.setOnClickListener {
            if(txtid.text.toString().trim().isEmpty()){
                ocultarTeclado()
                Toast.makeText(this@MainActivity, "Digite un id a buscar", Toast.LENGTH_SHORT).show()
            }else{
                val id = txtid.text.toString().toIntOrNull() ?: 0
                val db = FirebaseDatabase.getInstance()
                val dbref = db.getReference(Luchador::class.simpleName!!)

                dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val aux: Int = txtid.text.toString().toIntOrNull() ?: 0
                        var res: Boolean = false

                        for (x in dataSnapshot.children) {
                            val idFromSnapshot = x.child("id").getValue(String::class.java) // Obtener el ID del snapshot
                            if (idFromSnapshot != null && aux == idFromSnapshot.toIntOrNull()) {
                                res = true
                                ocultarTeclado()
                                txtnom.setText(x.child("nombre").getValue(String::class.java)) // Establecer el nombre en el EditText
                                break // Salir del bucle una vez que se encuentra el ID
                            }
                        }

                        if (!res) {
                            ocultarTeclado()
                            Toast.makeText(this@MainActivity, "No existe el id", Toast.LENGTH_SHORT).show()
                        }



                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Manejar el evento de cancelación
                    }
                })
            }
        }
    }

    private fun botonModificar() {}

    private fun botonRegistrar() {
        btnreg.setOnClickListener {
            if (txtid.text.toString().trim().isEmpty()
                || txtnom.text.toString().trim().isEmpty()
            ) {
                ocultarTeclado()
                Toast.makeText(this@MainActivity, "Complete los campos faltantes", Toast.LENGTH_SHORT).show()
            }else{
                val id: Int = txtid.text.toString().toIntOrNull() ?: 0
                val nom=txtnom.text.toString()

                val db = FirebaseDatabase.getInstance()
                val dbref = db.getReference(Luchador::class.simpleName!!)

                dbref.addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val aux: String = id.toString()
                        var res: Boolean = false

                        for (x in dataSnapshot.children) {
                            if (x.child("id").getValue().toString().equals(aux, ignoreCase = true)) {
                                res = true
                                ocultarTeclado()
                                Toast.makeText(this@MainActivity, "Error, el id ya existe!!", Toast.LENGTH_SHORT).show()
                                break;
                            }
                        }
                        var res2: Boolean = false

                        for (x in dataSnapshot.children) {
                            if (x.child("nombre").getValue().toString().equals(nom, ignoreCase = true)) {
                                res2 = true
                                ocultarTeclado()
                                Toast.makeText(this@MainActivity, "Error, el nombre ya existe!!", Toast.LENGTH_SHORT).show()
                                break;
                            }
                        }
                        if(res==false && res2==false){
                            val luc = Luchador(id, nom)
                            dbref.push().setValue(luc)
                            ocultarTeclado()
                            Toast.makeText(this@MainActivity, "Luchador Registrado Correctamente!!", Toast.LENGTH_SHORT).show()
                            txtid.setText("")
                            txtnom.setText("")
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Manejar el evento de cancelación
                    }
                })

            }
        }
    }

    private fun listarLuchadores(){
        val db = FirebaseDatabase.getInstance()
        val dbref = db.getReference(Luchador::class.simpleName!!)

        val lisluc = ArrayList<Luchador>()
        val ada = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, lisluc)
        lvDatos.setAdapter(ada)


        dbref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val luc = snapshot.getValue(Luchador::class.java)
                lisluc.add(luc!!)
                ada.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                ada.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Manejar evento de eliminación de hijo
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Manejar evento de movimiento de hijo
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar evento de cancelación
            }
        })

        lvDatos.setOnItemClickListener { parent, view, position, id ->
            val luc = lisluc[position]
            val a = AlertDialog.Builder(this@MainActivity)
            a.setCancelable(true)
            a.setTitle("Luchador Seleccionado")
            var msg = "ID: ${luc.id}\n\n"
            msg += "NOMBRE: ${luc.nombre}"
            a.setMessage(msg)
            a.show()
        }

    }

    private fun botonEliminar() {
        btneli.setOnClickListener {
            if(txtid.text.toString().trim().isEmpty()){
                ocultarTeclado()
                Toast.makeText(this@MainActivity, "Digite un id a eliminar", Toast.LENGTH_SHORT).show()
            }else{
                val id = txtid.text.toString().toIntOrNull() ?: 0
                val db = FirebaseDatabase.getInstance()
                val dbref = db.getReference(Luchador::class.simpleName!!)

                dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val aux: Int = txtid.text.toString().toIntOrNull() ?: 0
                        var res: Boolean = false

                        for (x in dataSnapshot.children) {
                            val idFromSnapshot = x.child("id").getValue(String::class.java) // Obtener el ID del snapshot
                            if (idFromSnapshot != null && aux == idFromSnapshot.toIntOrNull()) {
                                res = true
                                ocultarTeclado()
                                x.getRef().removeValue()
                                listarLuchadores()
                                break // Salir del bucle una vez que se encuentra el ID
                            }
                        }

                        if (!res) {
                            ocultarTeclado()
                            Toast.makeText(this@MainActivity, "No existe el id", Toast.LENGTH_SHORT).show()
                        }



                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Manejar el evento de cancelación
                    }
                })
            }
        }
    }



    private fun ocultarTeclado() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


}