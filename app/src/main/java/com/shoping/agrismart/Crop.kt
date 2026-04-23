package com.shoping.agrismart

data class Crop(
    val name: String,
    val type: String,
    val soilType: String,
    val description: String,
    val plantingSeason: String,
    val waterRequirement: String,
    val fertilizer: String,
    val commonPests: String,
    val idealTemp: String,
    val phLevel: String,
    val imageUrl: String // Changed from imageResId to imageUrl
)

object CropData {
    val crops = listOf(
        // Grains
        Crop("Rice", "Grain", "Clayey or Loamy", "Rice is a staple food. It requires standing water and a hot, humid climate.", "Kharif (June-July)", "Very High", "NPK", "Stem Borer", "20°C - 35°C", "5.5 - 6.5", "https://images.unsplash.com/photo-1586201357826-12bbdec8a9df?w=800"),
        Crop("Wheat", "Grain", "Well-drained Loamy", "Wheat is a winter crop. It needs cool weather during growth.", "Rabi (Oct-Nov)", "Moderate", "NPK, Sulfur", "Rust, Aphids", "10°C - 25°C", "6.0 - 7.5", "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=800"),
        Crop("Maize", "Grain", "Old Alluvial", "Grows well in 21°C to 27°C. Used as food and fodder.", "Kharif / Rabi", "Moderate", "Nitrogen-rich", "Fall Armyworm", "21°C - 27°C", "5.8 - 7.0", "https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=800"),
        
        // Vegetables
        Crop("Tomato", "Vegetable", "Well-drained Sandy Loam", "Needs plenty of sunlight and regular watering.", "Sept-Oct / Jan-Feb", "Regular", "Compost, NPK", "Fruit Borer", "18°C - 27°C", "6.0 - 6.8", "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=800"),
        Crop("Potato", "Vegetable", "Loose Aerated Soil", "Requires cool weather and well-drained soil.", "Oct-Nov", "Moderate", "Nitrogen, Potassium", "Late Blight", "15°C - 20°C", "5.0 - 6.0", "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=800"),
        Crop("Onion", "Vegetable", "Sandy Loam", "Needs mild climate without extreme heat or cold.", "Nov-Dec", "Moderate", "NPK, Sulfur", "Thrips", "13°C - 24°C", "6.0 - 7.0", "https://images.unsplash.com/photo-1508747703725-719777637510?w=800"),
        Crop("Carrot", "Vegetable", "Deep Sandy Loam", "Root vegetable, needs loose soil for growth.", "Oct-Nov", "Moderate", "Potassium-rich", "Rust Fly", "15°C - 21°C", "6.0 - 6.8", "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=800"),
        Crop("Spinach", "Vegetable", "Rich Loamy", "Leafy green, grows fast in cool weather.", "Sept-Oct", "High", "Nitrogen-rich", "Aphids", "10°C - 25°C", "6.5 - 7.5", "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=800"),
        Crop("Brinjal", "Vegetable", "Well-drained Silt Loam", "Also known as Eggplant, needs warm climate.", "Year-round", "Regular", "NPK, Manure", "Shoot Borer", "20°C - 30°C", "5.5 - 6.6", "https://images.unsplash.com/photo-1628556270448-4d4e4148e1b1?w=800"),

        // Fruits
        Crop("Mango", "Fruit", "Deep Alluvial/Loamy", "The King of Fruits. Needs hot, dry season for flowering.", "June-July (Planting)", "Moderate", "NPK, Organic", "Mango Hopper", "24°C - 30°C", "5.5 - 7.5", "https://images.unsplash.com/photo-1553279768-865429fa0078?w=800"),
        Crop("Banana", "Fruit", "Rich Well-drained Soil", "Requires warm, humid climate and plenty of water.", "Year-round", "High", "Potash-rich", "Aphids, Weevil", "20°C - 35°C", "6.5 - 7.5", "https://images.unsplash.com/photo-1571771894821-ad996211fdf4?w=800"),
        Crop("Apple", "Fruit", "Well-drained Loamy", "Grown in hilly regions with cool climate.", "Jan-Feb (Planting)", "Moderate", "NPK", "Woolly Aphid", "15°C - 25°C", "5.8 - 7.0", "https://images.unsplash.com/photo-1560806887-1e47018c6ee7?w=800"),
        Crop("Grapes", "Fruit", "Sandy to Clay Loam", "Needs hot and dry climate for ripening.", "Oct-Nov", "Moderate", "NPK, Manure", "Mealy Bug", "15°C - 40°C", "6.5 - 8.5", "https://images.unsplash.com/photo-1537640538966-79f369b41f8f?w=800"),
        Crop("Guava", "Fruit", "Wide range of soils", "Hardy fruit tree, survives in various conditions.", "June-July", "Low", "NPK", "Fruit Fly", "20°C - 28°C", "4.5 - 8.2", "https://images.unsplash.com/photo-1536511132770-0504856f685c?w=800"),
        Crop("Papaya", "Fruit", "Well-drained Sandy Loam", "Fast-growing tree, fruit is rich in Vitamin A.", "Year-round", "Regular", "NPK", "Whitefly", "25°C - 30°C", "6.0 - 6.5", "https://images.unsplash.com/photo-1526609118174-3528a4736191?w=800"),

        // Others
        Crop("Cotton", "Fiber", "Black Soil", "Needs high temperature and 210 frost-free days.", "Kharif", "Moderate", "Urea, DAP", "Bollworm", "21°C - 30°C", "6.0 - 8.5", "https://images.unsplash.com/photo-1594904351111-a072f80b1a71?w=800"),
        Crop("Sugarcane", "Cash Crop", "Deep Rich Loamy", "Takes almost a year to grow. Needs high temp.", "Annual", "High", "NPK", "Top Borer", "21°C - 27°C", "6.5 - 7.5", "https://images.unsplash.com/photo-1593109355172-871788755b3f?w=800"),
        Crop("Tea", "Beverage", "Deep Well-drained", "Requires warm and moist frost-free climate.", "Year-round", "High", "Ammonium Sulphate", "Red Spider Mite", "20°C - 30°C", "4.5 - 5.5", "https://images.unsplash.com/photo-1544739313-6fad02872377?w=800")
    )
}
