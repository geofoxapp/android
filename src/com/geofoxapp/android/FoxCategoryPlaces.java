package com.geofoxapp.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;


public class FoxCategoryPlaces {
	
	public String category_short;
	public boolean smallImagesLoaded;
	private ArrayList<FoxPlace> places;
	
	public FoxCategoryPlaces(String cat, JSONArray jsonplaces) throws FoxServerException
	{
		category_short = cat;
		places = new ArrayList<FoxPlace>();
		for(int i =0; i < jsonplaces.length(); i++)
		{
			try
			{
				places.add(new FoxPlace(jsonplaces.getJSONObject(i), false, false));
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
		}
		
		smallImagesLoaded = false;
	}
	
	public ArrayList<FoxPlace> getPlaces()
	{
		return places;
	}
	
	public void loadSmallImages()
	{
		if(smallImagesLoaded)
			return;
		
		for(int i=0;i < places.size(); i++)
		{
			places.get(i).loadSmallImageBitmaps();			
		}
		
		smallImagesLoaded = true;
	}
	
	
	@Override
	public String toString()
	{
		return categoryMap.get(category_short);
	}
	
	
	private static final Map<String, String> categoryMap;
	static
	{
		Map<String, String> aMap = new HashMap<String,String>();
		aMap.put("active", "Active Life");
		aMap.put("amateursportsteams", "Amateur Sports Teams");
		aMap.put("amusementparks", "Amusement Parks");
		aMap.put("aquariums", "Aquariums");
		aMap.put("archery", "Archery");
		aMap.put("beaches", "Beaches");
		aMap.put("bikerentals", "Bike Rentals");
		aMap.put("boating", "Boating");
		aMap.put("bowling", "Bowling");
		aMap.put("climbing", "Climbing");
		aMap.put("discgolf", "Disc Golf");
		aMap.put("diving", "Diving");
		aMap.put("fishing", "Fishing");
		aMap.put("fitness", "Fitness & Instruction");
		aMap.put("dancestudio", "Dance Studios");
		aMap.put("gyms", "Gyms");
		aMap.put("martialarts", "Martial Arts");
		aMap.put("pilates", "Pilates");
		aMap.put("swimminglessons", "Swimming Lessons/Schools");
		aMap.put("taichi", "Tai Chi");
		aMap.put("healthtrainers", "Trainers");
		aMap.put("yoga", "Yoga");
		aMap.put("gokarts", "Go Karts");
		aMap.put("golf", "Golf");
		aMap.put("gun_ranges", "Gun/Rifle Ranges");
		aMap.put("hiking", "Hiking");
		aMap.put("horseracing", "Horse Racing");
		aMap.put("horsebackriding", "Horseback Riding");
		aMap.put("lakes", "Lakes");
		aMap.put("leisure_centers", "Leisure Centers");
		aMap.put("mini_golf", "Mini Golf");
		aMap.put("parks", "Parks");
		aMap.put("dog_parks", "Dog Parks");
		aMap.put("skate_parks", "Skate Parks");
		aMap.put("playgrounds", "Playgrounds");
		aMap.put("rafting", "Rafting/Kayaking");
		aMap.put("skatingrinks", "Skating Rinks");
		aMap.put("skydiving", "Skydiving");
		aMap.put("football", "Soccer");
		aMap.put("sports_clubs", "Sports Clubs");
		aMap.put("summer_camps", "Summer Camps");
		aMap.put("swimmingpools", "Swimming Pools");
		aMap.put("tennis", "Tennis");
		aMap.put("zoos", "Zoos");
		aMap.put("arts", "Arts & Entertainment");
		aMap.put("arcades", "Arcades");
		aMap.put("galleries", "Art Galleries");
		aMap.put("gardens", "Botanical Gardens");
		aMap.put("casinos", "Casinos");
		aMap.put("movietheaters", "Cinema");
		aMap.put("festivals", "Festivals");
		aMap.put("jazzandblues", "Jazz & Blues");
		aMap.put("museums", "Museums");
		aMap.put("musicvenues", "Music Venues");
		aMap.put("opera", "Opera & Ballet");
		aMap.put("theater", "Performing Arts");
		aMap.put("sportsteams", "Professional Sports Teams");
		aMap.put("psychic_astrology", "Psychics & Astrologers");
		aMap.put("social_clubs", "Social Clubs");
		aMap.put("stadiumsarenas", "Stadiums & Arenas");
		aMap.put("wineries", "Wineries");
		aMap.put("auto", "Automotive");
		aMap.put("auto_detailing", "Auto Detailing");
		aMap.put("autoglass", "Auto Glass Services");
		aMap.put("autopartssupplies", "Auto Parts & Supplies");
		aMap.put("autorepair", "Auto Repair");
		aMap.put("bodyshops", "Body Shops");
		aMap.put("car_dealers", "Car Dealers");
		aMap.put("carwash", "Car Wash");
		aMap.put("servicestations", "Gas & Service Stations");
		aMap.put("motorcycledealers", "Motorcycle Dealers");
		aMap.put("motorcyclerepair", "Motorcycle Repair");
		aMap.put("oilchange", "Oil Change Stations");
		aMap.put("parking", "Parking");
		aMap.put("smog_check_stations", "Smog Check Stations");
		aMap.put("stereo_installation", "Stereo Installation");
		aMap.put("tires", "Tires");
		aMap.put("towing", "Towing");
		aMap.put("windshieldinstallrepair", "Windshield Installation & Repair");
		aMap.put("beautysvc", "Beauty and Spas");
		aMap.put("barbers", "Barbers");
		aMap.put("cosmetics", "Cosmetics & Beauty Supply");
		aMap.put("spas", "Day Spas");
		aMap.put("eyelashservice", "Eyelash Service");
		aMap.put("hairremoval", "Hair Removal");
		aMap.put("laser_hair_removal", "Laser Hair Removal");
		aMap.put("hair", "Hair Salons");
		aMap.put("makeupartists", "Makeup Artists");
		aMap.put("massage", "Massage");
		aMap.put("medicalspa", "Medical Spas");
		aMap.put("othersalons", "Nail Salons");
		aMap.put("piercing", "Piercing");
		aMap.put("skincare", "Skin Care");
		aMap.put("tanning", "Tanning");
		aMap.put("tattoo", "Tattoo");
		aMap.put("education", "Education");
		aMap.put("adultedu", "Adult Education");
		aMap.put("collegeuniv", "Colleges & Universities");
		aMap.put("educationservices", "Educational Services");
		aMap.put("elementaryschools", "Elementary Schools");
		aMap.put("highschools", "Middle Schools & High Schools");
		aMap.put("preschools", "Preschools");
		aMap.put("privatetutors", "Private Tutors");
		aMap.put("specialtyschools", "Specialty Schools");
		aMap.put("artschools", "Art Schools");
		aMap.put("cookingschools", "Cooking Schools");
		aMap.put("cosmetology_schools", "Cosmetology Schools");
		aMap.put("dance_schools", "Dance Schools");
		aMap.put("driving_schools", "Driving Schools");
		aMap.put("flightinstruction", "Flight Instruction");
		aMap.put("language_schools", "Language Schools");
		aMap.put("massage_schools", "Massage Schools");
		aMap.put("swimminglessons", "Swimming Lessons/Schools");
		aMap.put("tutoring", "Tutoring Centers");
		aMap.put("eventservices", "Event Planning & Services");
		aMap.put("boatcharters", "Boat Charters");
		aMap.put("stationery", "Cards & Stationery");
		aMap.put("catering", "Caterers");
		aMap.put("djs", "DJs");
		aMap.put("hotels", "Hotels");
		aMap.put("eventplanning", "Party & Event Planning");
		aMap.put("partysupplies", "Party Supplies");
		aMap.put("personalchefs", "Personal Chefs");
		aMap.put("photographers", "Photographers");
		aMap.put("venues", "Venues & Event Spaces");
		aMap.put("videographers", "Videographers");
		aMap.put("wedding_planning", "Wedding Planning");
		aMap.put("financialservices", "Financial Services");
		aMap.put("banks", "Banks & Credit Unions");
		aMap.put("paydayloans", "Check Cashing/Pay-day Loans");
		aMap.put("financialadvising", "Financial Advising");
		aMap.put("insurance", "Insurance");
		aMap.put("investing", "Investing");
		aMap.put("food", "Food");
		aMap.put("bagels", "Bagels");
		aMap.put("bakeries", "Bakeries");
		aMap.put("beer_and_wine", "Beer); Wine & Spirits");
		aMap.put("breweries", "Breweries");
		aMap.put("coffee", "Coffee & Tea");
		aMap.put("convenience", "Convenience Stores");
		aMap.put("desserts", "Desserts");
		aMap.put("diyfood", "Do-It-Yourself Food");
		aMap.put("donuts", "Donuts");
		aMap.put("farmersmarket", "Farmers Market");
		aMap.put("fooddeliveryservices", "Food Delivery Services");
		aMap.put("grocery", "Grocery");
		aMap.put("icecream", "Ice Cream & Frozen Yogurt");
		aMap.put("internetcafe", "Internet Cafes");
		aMap.put("juicebars", "Juice Bars & Smoothies");
		aMap.put("gourmet", "Specialty Food");
		aMap.put("candy", "Candy Stores");
		aMap.put("cheese", "Cheese Shops");
		aMap.put("chocolate", "Chocolatiers and Shops");
		aMap.put("ethnicmarkets", "Ethnic Food");
		aMap.put("markets", "Fruits & Veggies");
		aMap.put("healthmarkets", "Health Markets");
		aMap.put("meats", "Meat Shops");
		aMap.put("seafoodmarkets", "Seafood Markets");
		aMap.put("tea", "Tea Rooms");
		aMap.put("wineries", "Wineries");
		aMap.put("health", "Health and Medical");
		aMap.put("acupuncture", "Acupuncture");
		aMap.put("cannabis_clinics", "Cannabis Clinics");
		aMap.put("chiropractors", "Chiropractors");
		aMap.put("c_and_mh", "Counseling & Mental Health");
		aMap.put("dentists", "Dentists");
		aMap.put("cosmeticdentists", "Cosmetic Dentists");
		aMap.put("endodontists", "Endodontists");
		aMap.put("oralsurgeons", "Oral Surgeons");
		aMap.put("orthodontists", "Orthodontists");
		aMap.put("pediatric_dentists", "Pediatric Dentists");
		aMap.put("periodontists", "Periodontists");
		aMap.put("physicians", "Doctors");
		aMap.put("allergist", "Allergists");
		aMap.put("cardiology", "Cardiologists");
		aMap.put("cosmeticsurgeons", "Cosmetic Surgeons");
		aMap.put("dermatology", "Dermatologists");
		aMap.put("earnosethroat", "Ear Nose & Throat");
		aMap.put("familydr", "Family Practice");
		aMap.put("fertility", "Fertility");
		aMap.put("gerontologist", "Gerontologists");
		aMap.put("internalmed", "Internal Medicine");
		aMap.put("naturopathic", "Naturopathic/Holistic");
		aMap.put("obgyn", "Obstetricians and Gynecologists");
		aMap.put("opthamalogists", "Ophthalmologists");
		aMap.put("orthopedists", "Orthopedists");
		aMap.put("osteopathicphysicians", "Osteopathic Physicians");
		aMap.put("pediatricians", "Pediatricians");
		aMap.put("podiatrists", "Podiatrists");
		aMap.put("proctologist", "Proctologists");
		aMap.put("psychiatrists", "Psychiatrists");
		aMap.put("sportsmed", "Sports Medicine");
		aMap.put("tattooremoval", "Tattoo Removal");
		aMap.put("homehealthcare", "Home Health Care");
		aMap.put("hospitals", "Hospitals");
		aMap.put("laserlasikeyes", "Laser Eye Surgery/Lasik");
		aMap.put("medcenters", "Medical Centers");
		aMap.put("medicalspa", "Medical Spas");
		aMap.put("midwives", "Midwives");
		aMap.put("nutritionists", "Nutritionists");
		aMap.put("optometrists", "Optometrists");
		aMap.put("physicaltherapy", "Physical Therapy");
		aMap.put("retirement_homes", "Retirement Homes");
		aMap.put("speech_therapists", "Speech Therapists");
		aMap.put("tcm", "Traditional Chinese Medicine");
		aMap.put("urgent_care", "Urgent Care");
		aMap.put("weightlosscenters", "Weight Loss Centers");
		aMap.put("homeservices", "Home Services");
		aMap.put("buildingsupplies", "Building Supplies");
		aMap.put("carpetinstallation", "Carpet Installation");
		aMap.put("carpeting", "Carpeting");
		aMap.put("contractors", "Contractors");
		aMap.put("electricians", "Electricians");
		aMap.put("flooring", "Flooring");
		aMap.put("gardeners", "Gardeners");
		aMap.put("handyman", "Handyman");
		aMap.put("hvac", "Heating & Air Conditioning/HVAC");
		aMap.put("homecleaning", "Home Cleaning");
		aMap.put("home_inspectors", "Home Inspectors");
		aMap.put("hometheatreinstallation", "Home Theatre Installation");
		aMap.put("interiordesign", "Interior Design");
		aMap.put("isps", "Internet Service Providers");
		aMap.put("locksmiths", "Keys & Locksmiths");
		aMap.put("landscapearchitects", "Landscape Architects");
		aMap.put("landscaping", "Landscaping");
		aMap.put("lighting", "Lighting Fixtures & Equipment");
		aMap.put("movers", "Movers");
		aMap.put("painters", "Painters");
		aMap.put("plumbing", "Plumbing");
		aMap.put("poolcleaners", "Pool Cleaners");
		aMap.put("realestate", "Real Estate");
		aMap.put("apartments", "Apartments");
		aMap.put("homestaging", "Home Staging");
		aMap.put("mortgagebrokers", "Mortgage Brokers");
		aMap.put("propertymgmt", "Property Management");
		aMap.put("realestateagents", "Real Estate Agents");
		aMap.put("realestatesvcs", "Real Estate Services");
		aMap.put("university_housing", "University Housing");
		aMap.put("roofing", "Roofing");
		aMap.put("securitysystems", "Security Systems");
		aMap.put("blinds", "Shades & Blinds");
		aMap.put("solarinstallation", "Solar Installation");
		aMap.put("televisionserviceproviders", "Television Service Providers");
		aMap.put("treeservices", "Tree Services");
		aMap.put("windowwashing", "Window Washing");
		aMap.put("windowsinstallation", "Windows Installation");
		aMap.put("hotelstravel", "Hotels & Travel");
		aMap.put("airports", "Airports");
		aMap.put("bedbreakfast", "Bed & Breakfast");
		aMap.put("campgrounds", "Campgrounds");
		aMap.put("carrental", "Car Rental");
		aMap.put("guesthouses", "Guest Houses");
		aMap.put("hostels", "Hostels");
		aMap.put("hotels", "Hotels");
		aMap.put("rvrental", "RV Rental");
		aMap.put("skiresorts", "Ski Resorts");
		aMap.put("tours", "Tours");
		aMap.put("transport", "Transportation");
		aMap.put("airlines", "Airlines");
		aMap.put("airport_shuttles", "Airport Shuttles");
		aMap.put("limos", "Limos");
		aMap.put("publictransport", "Public Transportation");
		aMap.put("taxis", "Taxis");
		aMap.put("travelservices", "Travel Services");
		aMap.put("vacationrentalagents", "Vacation Rental Agents");
		aMap.put("localflavor", "Local Flavor");
		aMap.put("localservices", "Local Services");
		aMap.put("homeappliancerepair", "Appliances & Repair");
		aMap.put("carpet_cleaning", "Carpet Cleaning");
		aMap.put("childcare", "Child Care & Day Care");
		aMap.put("nonprofit", "Community Service/Non-Profit");
		aMap.put("couriers", "Couriers & Delivery Services");
		aMap.put("drycleaninglaundry", "Dry Cleaning & Laundry");
		aMap.put("electronicsrepair", "Electronics Repair");
		aMap.put("funeralservices", "Funeral Services & Cemeteries");
		aMap.put("reupholstery", "Furniture Reupholstery");
		aMap.put("itservices", "IT Services & Computer Repair");
		aMap.put("junkremovalandhauling", "Junk Removal and Hauling");
		aMap.put("notaries", "Notaries");
		aMap.put("pest_control", "Pest Control");
		aMap.put("copyshops", "Printing Services");
		aMap.put("recording_studios", "Recording & Rehearsal Studios");
		aMap.put("recyclingcenter", "Recycling Center");
		aMap.put("selfstorage", "Self Storage");
		aMap.put("sewingalterations", "Sewing & Alterations");
		aMap.put("shipping_centers", "Shipping Centers");
		aMap.put("shoerepair", "Shoe Repair");
		aMap.put("watch_repair", "Watch Repair");
		aMap.put("massmedia", "Mass Media");
		aMap.put("printmedia", "Print Media");
		aMap.put("radiostations", "Radio Stations");
		aMap.put("televisionstations", "Television Stations");
		aMap.put("nightlife", "Nightlife");
		aMap.put("adultentertainment", "Adult Entertainment");
		aMap.put("bars", "Bars");
		aMap.put("champagne_bars", "Champagne Bars");
		aMap.put("divebars", "Dive Bars");
		aMap.put("gaybars", "Gay Bars");
		aMap.put("hookah_bars", "Hookah Bars");
		aMap.put("lounges", "Lounges");
		aMap.put("pubs", "Pubs");
		aMap.put("sportsbars", "Sports Bars");
		aMap.put("wine_bars", "Wine Bars");
		aMap.put("comedyclubs", "Comedy Clubs");
		aMap.put("danceclubs", "Dance Clubs");
		aMap.put("jazzandblues", "Jazz & Blues");
		aMap.put("karaoke", "Karaoke");
		aMap.put("musicvenues", "Music Venues");
		aMap.put("poolhalls", "Pool Halls");
		aMap.put("pets", "Pets");
		aMap.put("animalshelters", "Animal Shelters");
		aMap.put("petservices", "Pet Services");
		aMap.put("dogwalkers", "Dog Walkers");
		aMap.put("pet_sitting", "Pet Boarding/Pet Sitting");
		aMap.put("groomer", "Pet Groomers");
		aMap.put("pet_training", "Pet Training");
		aMap.put("petstore", "Pet Stores");
		aMap.put("vet", "Veterinarians");
		aMap.put("professional", "Professional Services");
		aMap.put("accountants", "Accountants");
		aMap.put("advertising", "Advertising");
		aMap.put("architects", "Architects");
		aMap.put("careercounseling", "Career Counseling");
		aMap.put("employmentagencies", "Employment Agencies");
		aMap.put("graphicdesign", "Graphic Design");
		aMap.put("isps", "Internet Service Providers");
		aMap.put("lawyers", "Lawyers");
		aMap.put("bankruptcy", "Bankruptcy");
		aMap.put("divorce", "Divorce and Family Law");
		aMap.put("general_litigation", "General Litigation");
		aMap.put("personal_injury", "Personal Injury");
		aMap.put("lifecoach", "Life Coach");
		aMap.put("marketing", "Marketing");
		aMap.put("officecleaning", "Office Cleaning");
		aMap.put("privateinvestigation", "Private Investigation");
		aMap.put("publicrelations", "Public Relations");
		aMap.put("videofilmproductions", "Video/Film Production");
		aMap.put("web_design", "Web Design");
		aMap.put("publicservicesgovt", "Public Services & Government");
		aMap.put("departmentsofmotorvehicles", "Departments of Motor Vehicles");
		aMap.put("landmarks", "Landmarks & Historical Buildings");
		aMap.put("libraries", "Libraries");
		aMap.put("policedepartments", "Police Departments");
		aMap.put("postoffices", "Post Offices");
		aMap.put("realestate", "Real Estate");
		aMap.put("apartments", "Apartments");
		aMap.put("homestaging", "Home Staging");
		aMap.put("mortgagebrokers", "Mortgage Brokers");
		aMap.put("propertymgmt", "Property Management");
		aMap.put("realestateagents", "Real Estate Agents");
		aMap.put("realestatesvcs", "Real Estate Services");
		aMap.put("university_housing", "University Housing");
		aMap.put("religiousorgs", "Religious Organizations");
		aMap.put("buddhist_temples", "Buddhist Temples");
		aMap.put("churches", "Churches");
		aMap.put("hindu_temples", "Hindu Temples");
		aMap.put("mosques", "Mosques");
		aMap.put("synagogues", "Synagogues");
		aMap.put("restaurants", "Restaurants");
		aMap.put("afghani", "Afghan");
		aMap.put("african", "African");
		aMap.put("newamerican", "American (New)");
		aMap.put("tradamerican", "American (Traditional)");
		aMap.put("argentine", "Argentine");
		aMap.put("asianfusion", "Asian Fusion");
		aMap.put("bbq", "Barbeque");
		aMap.put("basque", "Basque");
		aMap.put("belgian", "Belgian");
		aMap.put("brasseries", "Brasseries");
		aMap.put("brazilian", "Brazilian");
		aMap.put("breakfast_brunch", "Breakfast & Brunch");
		aMap.put("british", "British");
		aMap.put("buffets", "Buffets");
		aMap.put("burgers", "Burgers");
		aMap.put("burmese", "Burmese");
		aMap.put("cajun", "Cajun/Creole");
		aMap.put("cambodian", "Cambodian");
		aMap.put("caribbean", "Caribbean");
		aMap.put("cheesesteaks", "Cheesesteaks");
		aMap.put("chicken_wings", "Chicken Wings");
		aMap.put("chinese", "Chinese");
		aMap.put("dimsum", "Dim Sum");
		aMap.put("creperies", "Creperies");
		aMap.put("cuban", "Cuban");
		aMap.put("delis", "Delis");
		aMap.put("diners", "Diners");
		aMap.put("ethiopian", "Ethiopian");
		aMap.put("hotdogs", "Fast Food");
		aMap.put("filipino", "Filipino");
		aMap.put("fishnchips", "Fish & Chips");
		aMap.put("fondue", "Fondue");
		aMap.put("foodstands", "Food Stands");
		aMap.put("french", "French");
		aMap.put("gastropubs", "Gastropubs");
		aMap.put("german", "German");
		aMap.put("gluten_free", "Gluten-Free");
		aMap.put("greek", "Greek");
		aMap.put("halal", "Halal");
		aMap.put("hawaiian", "Hawaiian");
		aMap.put("himalayan", "Himalayan/Nepalese");
		aMap.put("hotdog", "Hot Dogs");
		aMap.put("hungarian", "Hungarian");
		aMap.put("indpak", "Indian");
		aMap.put("indonesian", "Indonesian");
		aMap.put("irish", "Irish");
		aMap.put("italian", "Italian");
		aMap.put("japanese", "Japanese");
		aMap.put("korean", "Korean");
		aMap.put("kosher", "Kosher");
		aMap.put("latin", "Latin American");
		aMap.put("raw_food", "Live/Raw Food");
		aMap.put("malaysian", "Malaysian");
		aMap.put("mediterranean", "Mediterranean");
		aMap.put("mexican", "Mexican");
		aMap.put("mideastern", "Middle Eastern");
		aMap.put("modern_european", "Modern European");
		aMap.put("mongolian", "Mongolian");
		aMap.put("moroccan", "Moroccan");
		aMap.put("pakistani", "Pakistani");
		aMap.put("persian", "Persian/Iranian");
		aMap.put("peruvian", "Peruvian");
		aMap.put("pizza", "Pizza");
		aMap.put("polish", "Polish");
		aMap.put("portuguese", "Portuguese");
		aMap.put("russian", "Russian");
		aMap.put("sandwiches", "Sandwiches");
		aMap.put("scandinavian", "Scandinavian");
		aMap.put("seafood", "Seafood");
		aMap.put("singaporean", "Singaporean");
		aMap.put("soulfood", "Soul Food");
		aMap.put("soup", "Soup");
		aMap.put("southern", "Southern");
		aMap.put("spanish", "Spanish");
		aMap.put("steak", "Steakhouses");
		aMap.put("sushi", "Sushi Bars");
		aMap.put("taiwanese", "Taiwanese");
		aMap.put("tapas", "Tapas Bars");
		aMap.put("tapasmallplates", "Tapas/Small Plates");
		aMap.put("tex-mex", "Tex-Mex");
		aMap.put("thai", "Thai");
		aMap.put("turkish", "Turkish");
		aMap.put("ukrainian", "Ukrainian");
		aMap.put("vegan", "Vegan");
		aMap.put("vegetarian", "Vegetarian");
		aMap.put("vietnamese", "Vietnamese");
		aMap.put("shopping", "Shopping");
		aMap.put("adult", "Adult");
		aMap.put("antiques", "Antiques");
		aMap.put("galleries", "Art Galleries");
		aMap.put("artsandcrafts", "Arts & Crafts");
		aMap.put("artsupplies", "Art Supplies");
		aMap.put("stationery", "Cards & Stationery");
		aMap.put("costumes", "Costumes");
		aMap.put("fabricstores", "Fabric Stores");
		aMap.put("framing", "Framing");
		aMap.put("baby_gear", "Baby Gear & Furniture");
		aMap.put("media", "Books); Mags); Music and Video");
		aMap.put("bookstores", "Bookstores");
		aMap.put("comicbooks", "Comic Books");
		aMap.put("musicvideo", "Music & DVD's");
		aMap.put("mags", "Newspapers & Magazines");
		aMap.put("videoandgames", "Videos and Video Game Rental");
		aMap.put("vinyl_records", "Vinyl Records");
		aMap.put("bridal", "Bridal");
		aMap.put("computers", "Computers");
		aMap.put("cosmetics", "Cosmetics & Beauty Supply");
		aMap.put("deptstores", "Department Stores");
		aMap.put("discountstore", "Discount Store");
		aMap.put("drugstores", "Drugstores");
		aMap.put("electronics", "Electronics");
		aMap.put("opticians", "Eyewear & Opticians");
		aMap.put("fashion", "Fashion");
		aMap.put("accessories", "Accessories");
		aMap.put("childcloth", "Children's Clothing");
		aMap.put("deptstores", "Department Stores");
		aMap.put("leather", "Leather Goods");
		aMap.put("lingerie", "Lingerie");
		aMap.put("maternity", "Maternity Wear");
		aMap.put("menscloth", "Men's Clothing");
		aMap.put("shoes", "Shoe Stores");
		aMap.put("sportswear", "Sports Wear");
		aMap.put("swimwear", "Swimwear");
		aMap.put("vintage", "Used); Vintage & Consignment");
		aMap.put("womenscloth", "Women's Clothing");
		aMap.put("flowers", "Flowers & Gifts");
		aMap.put("stationery", "Cards & Stationery");
		aMap.put("florists", "Florists");
		aMap.put("hobbyshops", "Hobby Shops");
		aMap.put("homeandgarden", "Home & Garden");
		aMap.put("appliances", "Appliances");
		aMap.put("furniture", "Furniture Stores");
		aMap.put("hardware", "Hardware Stores");
		aMap.put("homedecor", "Home Decor");
		aMap.put("hottubandpool", "Hot Tub and Pool");
		aMap.put("kitchenandbath", "Kitchen & Bath");
		aMap.put("mattresses", "Mattresses");
		aMap.put("gardening", "Nurseries & Gardening");
		aMap.put("jewelry", "Jewelry");
		aMap.put("knittingsupplies", "Knitting Supplies");
		aMap.put("luggage", "Luggage");
		aMap.put("mobilephones", "Mobile Phones");
		aMap.put("musicalinstrumentsandteachers", "Musical Instruments & Teachers");
		aMap.put("officeequipment", "Office Equipment");
		aMap.put("outlet_stores", "Outlet Stores");
		aMap.put("pawn", "Pawn Shops");
		aMap.put("personal_shopping", "Personal Shopping");
		aMap.put("photographystores", "Photography Stores & Services");
		aMap.put("shoppingcenters", "Shopping Centers");
		aMap.put("sportgoods", "Sporting Goods");
		aMap.put("bikes", "Bikes");
		aMap.put("outdoorgear", "Outdoor Gear");
		aMap.put("sportswear", "Sports Wear");
		aMap.put("thrift_stores", "Thrift Stores");
		aMap.put("tobaccoshops", "Tobacco Shops");
		aMap.put("toys", "Toy Stores");
		aMap.put("watches", "Watches");
		aMap.put("wholesale_stores", "Wholesale Stores");

		categoryMap = Collections.unmodifiableMap(aMap);
	}
}
