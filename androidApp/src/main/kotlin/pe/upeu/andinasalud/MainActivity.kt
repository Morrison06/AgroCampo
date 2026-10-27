package pe.upeu.andinasalud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent { MaterialTheme { PantallaAgro() } }
 }
}
private data class Registro(val id: Long, val titulo: String, val detalle: String, val datos: String = "")
private data class Respuesta(val codigo: Int, val cuerpo: String)
private fun solicitud(url: String, metodo: String = "GET", token: String = "", body: String? = null): Respuesta {
 val c = URL(url).openConnection() as HttpURLConnection
 try {
  c.requestMethod = metodo; c.connectTimeout = 10000; c.readTimeout = 10000
  if (token.isNotEmpty()) c.setRequestProperty("Authorization", "Bearer $token")
  if (body != null) { c.doOutput = true; c.setRequestProperty("Content-Type", "application/json"); c.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) } }
  val code = c.responseCode
  val stream = if (code in 200..299) c.inputStream else c.errorStream
  return Respuesta(code, stream?.bufferedReader()?.use { it.readText() } ?: "")
 } finally { c.disconnect() }
}
@Composable
private fun PantallaAgro() {
 var direccion by remember { mutableStateOf("http://127.0.0.1:8080") }
 var usuario by remember { mutableStateOf("") }
 var clave by remember { mutableStateOf("") }
 var token by remember { mutableStateOf("") }
 var nombre by remember { mutableStateOf("") }
 var error by remember { mutableStateOf("") }
 var cargando by remember { mutableStateOf(false) }
 var lotes by remember { mutableStateOf<List<Registro>>(emptyList()) }
 var labores by remember { mutableStateOf<List<Registro>>(emptyList()) }
 var seleccionado by remember { mutableStateOf<Registro?>(null) }
 var permisos by remember { mutableStateOf<Set<String>>(emptySet()) }
 var formulario by remember { mutableStateOf(false) }
 var editando by remember { mutableStateOf<Long?>(null) }
 var tipo by remember { mutableStateOf("RIEGO") }
 var fecha by remember { mutableStateOf("2026-10-27") }
 var responsable by remember { mutableStateOf("") }
 var cantidad by remember { mutableStateOf("") }
 var observacion by remember { mutableStateOf("") }
 var errorFormulario by remember { mutableStateOf("") }
 var eliminar by remember { mutableStateOf<Registro?>(null) }
 var confirmarCierre by remember { mutableStateOf(false) }
 var buscar by remember { mutableStateOf("") }
 val scope = rememberCoroutineScope()
 fun cargarLotes() { scope.launch {
  cargando = true; error = ""
  try {
   val r = withContext(Dispatchers.IO) { solicitud(direccion.trimEnd('/') + "/api/v1/lotes?pagina=0&tamanio=100", token = token) }
   if (r.codigo == 401) { token = ""; error = "La sesión venció. Vuelve a ingresar." }
   else if (r.codigo != 200) error = "No se pudo cargar la lista (${r.codigo})."
   else { val a = JSONObject(r.cuerpo).getJSONArray("contenido"); lotes = (0 until a.length()).map { val x=a.getJSONObject(it); Registro(x.getLong("id"),x.getString("nombre"),"${x.getString("codigo")} · ${x.getString("valle")} · ${x.getString("cultivo")}") } }
  } catch (e: Exception) { error = "No se pudo conectar. Revisa la dirección y la red." }
  cargando = false
 } }
 fun cargarLabores(lote: Registro) { scope.launch {
  seleccionado = lote; cargando = true; error = ""; labores = emptyList()
  try {
   val r=withContext(Dispatchers.IO) { solicitud(direccion.trimEnd('/') + "/api/v1/lotes/${lote.id}/labores?pagina=0&tamanio=100", token=token) }
   if (r.codigo==401) { token=""; seleccionado=null; error="La sesión venció. Vuelve a ingresar." }
   else if(r.codigo!=200) error="No se pudieron cargar las labores (${r.codigo})."
   else { val a=JSONObject(r.cuerpo).getJSONArray("contenido"); labores=(0 until a.length()).map { val x=a.getJSONObject(it); Registro(x.getLong("id"),x.getString("tipo"),"${x.getString("fecha")} · ${x.getString("responsable")} · cantidad: ${x.getDouble("cantidad")}",x.toString()) } }
  } catch(e: Exception) { error="No se pudo conectar. Revisa la red." }
  cargando=false
 } }
 fun operar(ruta: String, metodo: String, body: String? = null) { scope.launch {
  cargando=true; error="";errorFormulario=""
  try {
   val r=withContext(Dispatchers.IO){solicitud(direccion.trimEnd('/')+"/api/v1/"+ruta,metodo,token,body)}
   if(r.codigo in 200..299){formulario=false; seleccionado?.let { cargarLabores(it) }}
   else {
    val j=runCatching { JSONObject(r.cuerpo) }.getOrNull()
    val campos=j?.optJSONObject("validationErrors")
    val detalles=campos?.keys()?.asSequence()?.map { "$it: ${campos.optString(it)}" }?.joinToString("\n").orEmpty()
    val mensaje=j?.optString("message")?.takeIf { it.isNotBlank() } ?: "Error ${r.codigo}"
    error=mensaje;errorFormulario=if(detalles.isBlank()) mensaje else detalles
    if(r.codigo==401){token="";formulario=false;seleccionado=null}
   }
  } catch(e:Exception){error="Sin conexión: no se guardó el cambio. Intenta nuevamente.";errorFormulario=error}
  cargando=false
 } }
 if(formulario) AlertDialog(onDismissRequest={if(!cargando)formulario=false},title={Text(if(editando==null)"Registrar labor" else "Corregir labor")},text={
  Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
   Row { listOf("RIEGO","FUMIGACION","COSECHA").forEach { t -> TextButton(onClick={tipo=t}){Text(if(tipo==t)"✓ $t" else t)} } }
   OutlinedTextField(fecha,{fecha=it},label={Text("Fecha AAAA-MM-DD")})
   OutlinedTextField(responsable,{responsable=it},label={Text("Responsable")})
   OutlinedTextField(cantidad,{cantidad=it},label={Text("Cantidad")})
   OutlinedTextField(observacion,{observacion=it},label={Text("Observación")})
   if(errorFormulario.isNotBlank())Text(errorFormulario,color=MaterialTheme.colorScheme.error)
  }
 },confirmButton={TextButton(enabled=!cargando,onClick={
  val valor=cantidad.toDoubleOrNull()
  if(valor==null || !valor.isFinite() || valor<0 || responsable.isBlank() || !fecha.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) errorFormulario="Revisa fecha, responsable y cantidad."
  else {
   val b=JSONObject().put("loteId",seleccionado!!.id).put("tipo",tipo).put("fecha",fecha).put("responsable",responsable).put("cantidad",valor).put("observacion",observacion)
   operar(if(editando==null)"labores" else "labores/$editando",if(editando==null)"POST" else "PUT",b.toString())
  }
 }){Text("Guardar")}},dismissButton={TextButton(onClick={formulario=false}){Text("Cancelar")}})
 eliminar?.let { labor -> AlertDialog(onDismissRequest={eliminar=null},title={Text("¿Eliminar labor?")},text={Text("Esta operación elimina el registro del servidor.")},confirmButton={TextButton(onClick={eliminar=null;operar("labores/${labor.id}","DELETE")}){Text("Eliminar")}},dismissButton={TextButton(onClick={eliminar=null}){Text("Cancelar")}}) }
 if(confirmarCierre) AlertDialog(onDismissRequest={confirmarCierre=false},title={Text("¿Cerrar campaña?")},text={Text("Después no se podrán registrar ni corregir labores.")},confirmButton={TextButton(onClick={confirmarCierre=false;operar("lotes/${seleccionado!!.id}/cerrar-campania","POST")}){Text("Cerrar campaña")}},dismissButton={TextButton(onClick={confirmarCierre=false}){Text("Cancelar")}})
 Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
  Text("AgroCampo", style=MaterialTheme.typography.headlineMedium)
  Text("Gestión de labores agrícolas", style=MaterialTheme.typography.bodySmall)
  if(token.isEmpty()) {
   OutlinedTextField(direccion,{direccion=it},label={Text("Dirección del servidor")},singleLine=true,modifier=Modifier.fillMaxWidth())
   OutlinedTextField(usuario,{usuario=it},label={Text("Usuario")},singleLine=true,modifier=Modifier.fillMaxWidth())
   OutlinedTextField(clave,{clave=it},label={Text("Contraseña")},visualTransformation=PasswordVisualTransformation(),singleLine=true,modifier=Modifier.fillMaxWidth())
   Button(enabled=!cargando,onClick={ scope.launch {
    cargando=true;error=""
    try {
     val body=JSONObject().put("usuario",usuario).put("contrasena",clave).toString()
     val r=withContext(Dispatchers.IO){solicitud(direccion.trimEnd('/')+"/api/v1/auth/login","POST",body=body)}
     if(r.codigo==200){val j=JSONObject(r.cuerpo);token=j.getString("token");nombre=j.optString("nombre",usuario);val pr=j.getJSONArray("privilegios");permisos=(0 until pr.length()).map { pr.getString(it) }.toSet();clave="";cargarLotes()}
     else error=if(r.codigo==401) "Usuario o contraseña incorrectos." else "Error del servidor (${r.codigo})."
    } catch(e:Exception){error="No se pudo conectar. Revisa la dirección y la red."}
    cargando=false
   } }) { Text("Ingresar") }
  } else {
   Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
    Text("Hola, $nombre",modifier=Modifier.weight(1f))
    TextButton(onClick={token="";seleccionado=null;lotes=emptyList();labores=emptyList()}){Text("Salir")}
   }
   if(seleccionado==null){
    Row { Text("Lotes",style=MaterialTheme.typography.titleLarge,modifier=Modifier.weight(1f));TextButton(onClick={cargarLotes()}){Text("Actualizar")} }
    if(lotes.isEmpty() && !cargando) Text("No hay lotes disponibles.")
    OutlinedTextField(buscar,{buscar=it},label={Text("Buscar nombre, código, valle o cultivo")},modifier=Modifier.fillMaxWidth())
    LazyColumn(Modifier.weight(1f)) { items(lotes.filter { it.titulo.contains(buscar,true)||it.detalle.contains(buscar,true) }) { l -> Card(Modifier.fillMaxWidth().padding(vertical=4.dp).clickable { cargarLabores(l) }) { Column(Modifier.padding(16.dp)) { Text(l.titulo,style=MaterialTheme.typography.titleMedium);Text(l.detalle) } } } }
   } else {
    TextButton(onClick={seleccionado=null}){Text("← Volver a lotes")}
    Text(seleccionado!!.titulo,style=MaterialTheme.typography.titleLarge)
    if(labores.isEmpty() && !cargando) Text("No hay labores disponibles.")
    Row {
     if("LABOR_CREAR" in permisos) TextButton(enabled=!cargando,onClick={editando=null;tipo="RIEGO";responsable=nombre;cantidad="";observacion="";errorFormulario="";formulario=true}){Text("Registrar labor")}
     if("CAMPANIA_CERRAR" in permisos)TextButton(enabled=!cargando,onClick={confirmarCierre=true}){Text("Cerrar campaña")}
    }
    LazyColumn(Modifier.weight(1f)) { items(labores) { l -> Card(Modifier.fillMaxWidth().padding(vertical=4.dp)) { Column(Modifier.padding(16.dp)){
     Text(l.titulo);Text(l.detalle)
     Row {
      if("LABOR_EDITAR" in permisos)TextButton(enabled=!cargando,onClick={val j=JSONObject(l.datos);editando=l.id;tipo=j.getString("tipo");fecha=j.getString("fecha");responsable=j.getString("responsable");cantidad=j.getDouble("cantidad").toString();observacion=j.optString("observacion");errorFormulario="";formulario=true}){Text("Editar")}
      if("LABOR_ELIMINAR" in permisos)TextButton(enabled=!cargando,onClick={eliminar=l}){Text("Eliminar")}
     }
    } } } }
   }
  }
  if(cargando) CircularProgressIndicator()
  if(error.isNotBlank()) Text(error,color=MaterialTheme.colorScheme.error)
 }
}
