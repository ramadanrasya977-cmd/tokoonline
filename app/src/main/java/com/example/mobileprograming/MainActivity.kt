
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileprograming.ui.theme.MobileProgramingTheme

private val Green = Color(0xFF176B52)
private val Background = Color(0xFFF5F7F5)

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Int,
    val emoji: String
)

private val products = listOf(
    Product(1, "Sneakers Casual", "Fashion", 249000, "👟"),
    Product(2, "Kaos Basic", "Fashion", 89000, "👕"),
    Product(3, "Jam Tangan", "Aksesoris", 175000, "⌚"),
    Product(4, "Headphone", "Elektronik", 299000, "🎧"),
    Product(5, "Tas Minimalis", "Fashion", 159000, "🎒"),
    Product(6, "Smartphone", "Elektronik", 2499000, "📱"),
    Product(7, "Kacamata", "Aksesoris", 99000, "🕶️"),
    Product(8, "Sepatu Sport", "Fashion", 319000, "👟")
)

private fun rupiah(value: Int): String {
    return "Rp " + "%,d".format(value).replace(',', '.')
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileProgramingTheme {
                TokoOnlineApp()
            }
        }
    }
}

@Composable
fun TokoOnlineApp() {
    var selectedTab by remember { mutableStateOf(0) }
    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Semua") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val cart = remember { mutableStateMapOf<Int, Int>() }

    val filteredProducts = products.filter { product ->
        (category == "Semua" || product.category == category) &&
                product.name.contains(search, ignoreCase = true)
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0 && selectedProduct == null,
                    onClick = {
                        selectedTab = 0
                        selectedProduct = null
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        selectedProduct = null
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                val count = cart.values.sum()
                                if (count > 0) {
                                    Badge { Text(count.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Keranjang"
                            )
                        }
                    },
                    label = { Text("Keranjang") }
                )
            }
        }
    ) { padding ->
        when {
            selectedProduct != null -> {
                ProductDetail(
                    product = selectedProduct!!,
                    onBack = { selectedProduct = null },
                    onAdd = {
                        val id = selectedProduct!!.id
                        cart[id] = (cart[id] ?: 0) + 1
                    },
                    modifier = Modifier.padding(padding)
                )
            }

            selectedTab == 0 -> {
                HomeScreen(
                    search = search,
                    onSearchChange = { search = it },
                    category = category,
                    onCategoryChange = { category = it },
                    products = filteredProducts,
                    onProductClick = { selectedProduct = it },
                    onAdd = { product ->
                        cart[product.id] = (cart[product.id] ?: 0) + 1
                    },
                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                CartScreen(
                    cart = cart,
                    onIncrease = { id ->
                        cart[id] = (cart[id] ?: 0) + 1
                    },
                    onDecrease = { id ->
                        val qty = cart[id] ?: 0
                        if (qty <= 1) cart.remove(id)
                        else cart[id] = qty - 1
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    search: String,
    onSearchChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onAdd: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "TOKO ONLINE",
                        color = Green,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                    Text("Temukan produk favoritmu", color = Color.Gray)
                }
                Text("🛍️", fontSize = 34.sp)
            }
        }

        item {
            OutlinedTextField(
                value = search,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari produk...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Cari")
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Green),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "BELANJA LEBIH MUDAH",
                        color = Color(0xFFC8E8D7),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Upgrade gayamu\nmulai hari ini.",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 31.sp
                    )
                    Text(
                        "Pilihan menarik, harga bersahabat.",
                        color = Color.White
                    )
                }
            }
        }

        item {
            Text(
                "Kategori",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Semua", "Fashion", "Elektronik", "Aksesoris")) {
                    FilterChip(
                        selected = category == it,
                        onClick = { onCategoryChange(it) },
                        label = { Text(it) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Green,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        item {
            Text(
                "Produk Pilihan",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (products.isEmpty()) {
            item {
                Text(
                    "Produk tidak ditemukan.",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }

        items(products.chunked(2)) { rowProducts ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowProducts.forEach { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product) },
                        onAdd = { onAdd(product) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowProducts.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        item {
            Text(
                "Terima kasih sudah berbelanja 💚",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .background(Color(0xFFE8F1EB)),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 49.sp)
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    product.category.uppercase(),
                    fontSize = 10.sp,
                    color = Green,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    product.name,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    rupiah(product.price),
                    fontSize = 14.sp,
                    color = Green,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(
                        horizontal = 6.dp,
                        vertical = 4.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text("Tambah", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ProductDetail(
    product: Product,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        TextButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Kembali")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Color(0xFFE8F1EB),
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(product.emoji, fontSize = 100.sp)
        }

        Text(product.category, color = Green)
        Text(product.name, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text(
            rupiah(product.price),
            fontSize = 23.sp,
            color = Green,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Produk pilihan dengan desain menarik dan harga bersahabat.",
            color = Color.DarkGray,
            fontSize = 15.sp
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onAdd,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Green),
            shape = RoundedCornerShape(15.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Tambah ke Keranjang")
        }
    }
}

@Composable
fun CartScreen(
    cart: Map<Int, Int>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartProducts = products.filter { (cart[it.id] ?: 0) > 0 }
    val total = cartProducts.sumOf {
        it.price * (cart[it.id] ?: 0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {
        Text(
            "Keranjang Saya",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(18.dp))

        if (cartProducts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🛒", fontSize = 65.sp)
                Spacer(Modifier.height(12.dp))
                Text("Keranjang masih kosong", fontWeight = FontWeight.Bold)
                Text("Yuk, pilih produk favoritmu!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartProducts) { product ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(product.emoji, fontSize = 35.sp)
                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(
                                    product.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    rupiah(product.price),
                                    color = Green
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onDecrease(product.id) }
                                ) {
                                    Text("−", fontSize = 22.sp)
                                }
                                Text(
                                    (cart[product.id] ?: 0).toString(),
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { onIncrease(product.id) }
                                ) {
                                    Text("+", fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Belanja", fontSize = 17.sp)
                Text(
                    rupiah(total),
                    fontSize = 19.sp,
                    color = Green,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Lanjut Checkout")
            }

            Text(
                "Checkout masih berupa demo.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}