package com.personal.portfolio.ui.screens.admin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.portfolio.data.remote.* // Import các Model
import com.personal.portfolio.ui.theme.SakuraPrimary
import com.personal.portfolio.ui.theme.SakuraSecondary

// --- 1. HERO EDITOR ---
@Composable
fun HeroEditor(lang: String, data: HeroData, onUpdate: (HeroData) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("HERO ($lang)", color = SakuraPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            AdminTextField("Greeting", data.greeting) { onUpdate(data.copy(greeting = it)) }
            AdminTextField("Full Name", data.fullName) { onUpdate(data.copy(fullName = it)) }
            Row {
                Box(Modifier.weight(1f)) { AdminTextField("Nick 1", data.nickName1) { onUpdate(data.copy(nickName1 = it)) } }
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) { AdminTextField("Nick 2", data.nickName2) { onUpdate(data.copy(nickName2 = it)) } }
            }
            AdminTextField("Desc", data.description, singleLine = false) { onUpdate(data.copy(description = it)) }
        }
    }
}

// --- 2. BOX EDITOR (Profile/Contact) ---
@Composable
fun BoxEditor(lang: String, data: List<SectionBox>, onUpdate: (List<SectionBox>) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("PROFILE / CONTACT ($lang)", color = SakuraPrimary, fontWeight = FontWeight.Bold)

            data.forEachIndexed { bIdx, box ->
                Column(Modifier.padding(vertical = 8.dp).border(1.dp, SakuraSecondary, RoundedCornerShape(8.dp)).padding(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AdminTextField("Group Title", box.title, Modifier.weight(1f)) {
                            // Deep copy logic để trigger recompose
                            val newData = data.map { it.copy() }.toMutableList()
                            newData[bIdx] = box.copy(title = it)
                            onUpdate(newData)
                        }
                        IconButton(onClick = {
                            val n = data.toMutableList()
                            n.removeAt(bIdx)
                            onUpdate(n)
                        }) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                        }
                    }

                    box.items.forEachIndexed { iIdx, item ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AdminTextField("Label", item.label, Modifier.weight(1f)) {
                                val nData = data.map { it.copy() }
                                nData[bIdx].items[iIdx].label = it
                                onUpdate(nData)
                            }
                            Spacer(Modifier.width(4.dp))
                            AdminTextField("Value", item.value, Modifier.weight(1f)) {
                                val nData = data.map { it.copy() }
                                nData[bIdx].items[iIdx].value = it
                                onUpdate(nData)
                            }
                            IconButton(onClick = {
                                val nData = data.map { it.copy() }
                                nData[bIdx].items.removeAt(iIdx)
                                onUpdate(nData)
                            }) { Icon(Icons.Default.Delete, null, tint = Color.Gray) }
                        }
                    }
                    TextButton(onClick = {
                        val nData = data.map { it.copy() }
                        nData[bIdx].items.add(SectionBoxItem())
                        onUpdate(nData)
                    }) { Text("+ Add Item") }
                }
            }
            Button(onClick = { onUpdate(data + SectionBox(id = System.currentTimeMillis().toString(), items = mutableListOf())) }, colors = ButtonDefaults.buttonColors(containerColor = SakuraSecondary)) {
                Text("+ NEW GROUP")
            }
        }
    }
}

// --- HELPER: TEXT FIELD ---
@Composable
fun AdminTextField(label: String, value: String, modifier: Modifier = Modifier, singleLine: Boolean = true, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SakuraPrimary,
            unfocusedBorderColor = Color.LightGray
        )
    )
}