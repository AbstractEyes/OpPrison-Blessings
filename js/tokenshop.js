// TODO explosive integration

load("util:plugins.js");
load("util:format.js")
load("util:items.js")
load("util:menus.js")
load("network/util/items.js")
load("network/util/format.js")
//
importClass("org.pvpingmc.tokens.tokens.GUI")
importClass("org.pvpingmc.tokens.utils.ExpUtils")
importClass("org.pvpingmc.tokens.utils.BackpackUtils")
importClass("org.pvpingmc.backpacks.backpack.Backpack")
importClass("org.pvpingmc.backpacks.backpack.BackpackManager");

var RedItems = pluginGet("RedItems")
var RedItems = RedItems && RedItems
var absUtils = pluginGet("AbstractEnchantsOP")
var AbstractImbues = absUtils && absUtils.imbuedController();
var AbstractPassives = absUtils && absUtils.passivesController();

var enchutil = exports.ptr("setsenchantsutil")
var claimutil = exports.ptr("claimutil")
var currencyutil = exports.ptr("currencyutil")
var tokensutil = () => currencyutil()("@tokens")
var prestigetokensutil = () => currencyutil()("@prestigetokens")
var tokensSymbol = ""

var enchants = new ArrayList([
	{
		icon: itemEss("redstone 1"),
		title: "Haste",
		key: "haste",
		lore: ['&r&7&o"Allows you to swing your pickaxe faster while mining."'],
		price: 25,
		levelMax: 3,
		onBuy: doLoreEnchant("Haste"),
	},
	{
		icon: itemEss("sugar 1"),
		title: "Speed",
		key: "speed",
		lore: ['&7&o"Allows you to move faster while mining."'],
		price: 15,
		levelMax: 5,
		onBuy: doLoreEnchant("Speed"),
	},
	{
		icon: itemEss("eyeofender 1"),
		title: "Night Vision",
		key: "nightvision",
		lore: ['&7&o"Allows you to see in the dark while mining."'],
		price: 5,
		levelMax: 3,
		onBuy: doLoreEnchant("NightVision"),
	},
	{
		icon: itemEss("tnt 1"),
		title: "Explosive",
		key: "explosive",
		lore: ['&7&o"Explodes nearby blocks while mining."'],
		price: 500,
		levelMax: 3000,
		onBuy: doLoreEnchant("Explosive"),
	},
	{
		icon: itemEss("dpick 1"),
		title: "Efficiency",
		key: "efficiency",
		lore: ['&7&o"Allows you to mine faster."'],
		price: 3,
		levelMax: 500,
		onBuy: doEnchant(Enchantment.DIG_SPEED),
	},
	{
		icon: itemEss("diamond 1"),
		title: "Fortune",
		key: "fortune",
		lore: ['&7&o"Allows you to receive more blocks while mining."'],
		price: 75,
		levelMax: 25000,
		onBuy: doEnchant(Enchantment.LOOT_BONUS_BLOCKS),
	},
	{
		icon: itemEss("minecart 1"),
		title: "Auto Mine",
		key: "automine",
		lore: ['&7&o"Automatically mines nearby blocks while idle or mining."'],
		price: 100,
		levelMax: 200,
		onBuy: doLoreEnchant("AutoMine"),
	},
	{
		icon: itemEss("hopper 1"),
		title: "Drill Hammer",
		key: "drillerhammer",
		lore: ['&7&o"Chance to break a full layer of blocks in a mine."'],
		price: 800,
		levelMax: 400,
		onBuy: doLoreEnchant("DrillHammer"),
	},
	{
		icon: itemEss("deadbush 1"),
		title: "Looter",
		key: "looter",
		lore: ['&7&o"Chance to find tokens while mining blocks."'],
		price: 700,
		levelMax: 1100,
		onBuy: doLoreEnchant("Looter"),
	},
	{
		icon: itemEss("piston 1"),
		title: "OmniTool",
		key: "omnitool",
		lore: ['&7&o"Automatically switches your tool to the more suitable tool for the job."'],
		price: 2500,
		levelMax: 1,
		onBuy: doLoreEnchant("OmniTool"),
	},
	{
		icon: itemEss("xpbottle 1"),
		title: "Experience",
		key: "experience",
		lore: ['&7&o"Boosts Experience from Mobs & Blocks."'],
		price: 950,
		levelMax: 35,
		onBuy: doLoreEnchant("Experience"),
		allowWeapon: true,
	},
	{
		icon: itemEss("book 1"),
		title: "Lucky Duck",
		key: "luckyduck",
		lore: ['&7&o"Increases your chance to find a Lucky Cards."'],
		price: 200,
		levelMax: 500,
		onBuy: doLoreEnchant("LuckyDuck"),
	},
	{
		icon: itemEss("diamondblock 1"),
		title: "Double Trouble",
		key: "doubletrouble",
		lore: ['&7&o"Chance to double your blocks sold."'],
		price: 1000,
		levelMax: 350,
		onBuy: doLoreEnchant("DoubleTrouble"),
	},
	{
		icon: itemEss("beacon 1"),
		title: "Beacon Blast",
		key: "beaconblast",
		lore: ['&7&o"Chance to spawn a beacon in your inventory."'],
		price: 1000,
		levelMax: 275,
		onBuy: doLoreEnchant("BeaconBlast"),
	},
	/*{
		icon: itemEss("endportalframe 1"),
		title: "Laser",
		key: "laser",
		lore: ['&7&o"Chance to laser all the way to bedrock in a 3x3 radius."'],
		price: 50000,
		levelMax: 125,
		onBuy: doLoreEnchant("Laser"),
	},*/
	{
		icon: itemEss("tripwirehook 1"),
		title: "KeyMaster",
		key: "keymaster",
		lore: ['&7&o"Chance to earn keys whilst mining blocks."'],
		price: 750,
		levelMax: 3,
		onBuy: doLoreEnchant("KeyMaster"),
	},
])

// TODO POPULATE TOMORROW.
var abstractPassivesEnchants = new ArrayList([
	{
		icon: itemEss("diamond_leggings 1"),
		title: "Clout",
		key: "clout",
		lore: ['&r&7&o"Reduces token costs from the token shop."'],
		token: "token",
		price: 100,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("emerald 1"),
		title: "Token Baron",
		key: "tokenbaron",
		lore: ['&7&o"Increases the min/max token drops."'],
		token: "ptoken",
		price: 2,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("emerald_block 1"),
		title: "Very Fortunate",
		key: "veryfortunate",
		lore: ['&7&o"Increases fortune drop amounts."'],
		token: "token",
		price: 200,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("diamond_block 1"),
		title: "Very VERY fortunate",
		key: "veryveryfortunate",
		lore: ['&7&o"FURTHER increases fortune drop amounts."'],
		token: "ptoken",
		price: 6,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("sponge 1"),
		title: "Sponge Exchange",
		key: "spongeexchange",
		lore: ['&7&o"Provides a chance to mine sponges."'],
		token: "token",
		price: 100,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("beacon 1"),
		title: "Beacon Exchange",
		key: "beaconexchange",
		lore: ['&7&o"Provides a chance to mine beacons."'],
		token: "token",
		price: 150,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("dragonegg 1"),
		title: "Dragon Egg Exchange",
		key: "dragoneggexchange",
		lore: ['&7&o"Provides a chance to mine dragon eggs"'],
		token: "ptoken",
		price: 2,
		levelMax: 10000,
		onBuy: levelPassive(),
	},	
	{
		icon: itemEss("paper 1"),
		title: "Scratchers Ahoy",
		key: "scratchersahoy",
		lore: ['&7&o"Adds another chance to spawn a scratcher in your inventory."'],
		token: "token",
		price: 1000,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("chest 1"),
		title: "Magnetic Finder",
		key: "magneticfinder",
		lore: ['&7&o"Provides a higher chance to find keys while mining."'],
		token: "token",
		price: 1000,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("book 1"),
		title: "Blessing Enlighten",
		key: "passivetrainer",
		lore: ['&7&o"Chance to level up a random blessing while mining."'],
		token: "ptoken",
		price: 6,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("hopper 1"),
		title: "DrillDozer",
		key: "drilldozer",
		lore: ['&7&o"Drills another layer per level using DrillHammer."'],
		token: "token",
		price: 5000000,
		levelMax: 2,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("hopper 1"),
		title: "Armageddon Drilling",
		key: "armageddon",
		lore: ['&7&o"Improves the odds of DrillHammer activating"'],
		token: "token",
		price: 1000,
		levelMax: 10000,
		onBuy: levelPassive()
	},
	{
		icon: itemEss("tnt 1"),
		title: "Demolitions Expert",
		key: "demolitions",
		lore: ['&7&o"Improves chance of removing stray blocks', '&7&oleftover from Explosive enchanted pickaxes."'],
		token: "token",
		price: 250,
		levelMax: 10000,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("tnt 1"),
		title: "Cluster Bombs",
		key: "clusterbombs",
		lore: ['&7&o"Adds an additional 3x3x3 cluster bomb', '&7&oper level with Explosive enchanted pickaxes."'],
		token: "token",
		price: 1000000,
		levelMax: 10,
		onBuy: levelPassive(),
	},
	{
		icon: itemEss("SKULL_ITEM 1"),
		title: "Blessings Container",
		key: "blessing",
		lore: ['&7&o"You need one of these in order to learn blessings."'],
		token: "token",
		price: 1000,
		levelMax: 1,
		onBuy: addPassiveItem(),
	},
])
//
var enchantNmsCap = Short.MAX_VALUE
var purchaseRatelimitAttribute = exports.getOrDefault(cwd + "/purchaseRatelimit", bukkit.newAttribute())

var mainMenuMask = "000010000 001010100 000010000"
var mainMenuSchemeMask = "111101111 110101011 111101111"
var mainMenuScheme = menuSchemeFill([4], true)
var mainMenuWithdrawItem = itemStack(351, 1, 14)
itemGlow(mainMenuWithdrawItem)
itemAddLore(mainMenuWithdrawItem, colorize("&7&o(( Click to withdraw tokens ))"))
var mainMenuEXPItem = itemEss("expbottle 1")
itemGlow(mainMenuEXPItem)
itemAddLore(mainMenuEXPItem, colorize("&7&o(( Click to exchange ))"))
var mainMenuExplosiveItem = itemName(itemEss("tnt 1"), colorize("&e&l[!]&6 Explosives"))
itemGlow(mainMenuExplosiveItem)
itemAddLore(mainMenuExplosiveItem, colorize('&7&o"Boom your way through the mine."'))
var mainMenuToolItem = itemName(itemEss("book 1"), colorize("&e&l[!]&6 Enchants"))
itemGlow(mainMenuToolItem)
itemAddLore(mainMenuToolItem, colorize('&7&o"Customize your tools with special effects."'))
var mainMenuBackpackItem = itemName(itemEss("chest 1"), colorize("&e&l[!]&6 Backpacks"))
itemGlow(mainMenuBackpackItem)
itemAddLore(mainMenuBackpackItem, colorize('&7&o"Handle more blocks in your inventory."'))
var mainMenuPassiveItem = itemName(itemStack("SKULL_ITEM", 1, 3), colorize("&e&l[!]&6 Upgrade Blessings"))
itemGlow(mainMenuPassiveItem)
itemAddLore(mainMenuPassiveItem, colorize('&7&o"Unlock and upgrade powerful blessings here."'))
//
var enchantMenuMask = "000000000 011111110 011111110 000010001"
var enchantMenuSchemeMask = "111111111 111111111 111111111 111111111"
var enchantMenuScheme = menuSchemeFill([13], true)
var enchantDowngradeMenuScheme = menuSchemeFill([5], true)
var enchantDowngradeItem = itemName(itemEss("barrier 1"), colorize("&e&l[!]&6 Downgrade Tool"))
itemAddLore(enchantDowngradeItem, colorize('&7&o"Remove any unwanted special effects."'))
var enchantUpgradeItem = itemName(itemEss("book 1"), colorize("&e&l[!]&6 Upgrade Tool"))
itemAddLore(enchantUpgradeItem, colorize('&7&o"Enchant tools with special effects to use while mining!"'))
//
var passiveMenuMask = "000000000 011111110 011111110 000010011"
var passiveMenuSchemeMask = "111111111 111111111 111111111 111111111"
var passiveMenuScheme = menuSchemeFill([13], true)
var passiveDowngradeMenuScheme = menuSchemeFill([5], true)
var passiveDowngradeItem = itemName(itemEss("barrier 1"), colorize("&e&l[!]&6 Downgrade Tool"))
itemAddLore(passiveDowngradeItem, colorize('&7&o"Remove any unwanted special effects."'))
var passiveUpgradeItem = itemName(itemEss("book 1"), colorize("&e&l[!]&6 Upgrade Tool"))
itemAddLore(passiveUpgradeItem, colorize('&7&o"Unlock divine blessings here!"'))

var expMenuMask = "0 001000100 0"
var expMenuSchemeMask = "111111111 110111011 111111111"
var expMenuScheme = menuSchemeFill([13], true)
var expConvertPrice = 4000
var expConvertOneItem = itemName(itemEss("expbottle 1"), colorize("&e&l[!]&6 Exchange EXP"))
itemAddLore(expConvertOneItem, colorize('&7&o"Convert your experience into tokens!"'))
itemAddLore(expConvertOneItem, colorize("&6&l * &ePrice: &f" + formatCommas(expConvertPrice) + " Experience"))
itemAddLore(expConvertOneItem, colorize("&6&l * &eExchange: &f+1 Tokens"))
var expConvertAllItem = itemName(itemEss("expbottle 1"), colorize("&e&l[!]&6 Exchange ALL EXP"))
itemGlow(expConvertAllItem)
itemAddLore(expConvertAllItem, colorize('&7&o"Convert your experience into tokens!"'))

var backpackMenuMask = "0 00010100 000000001"
var backpackMenuSchemeMask = "111111111 111010111 111111110"
var backpackMenuScheme = menuSchemeFill([4], true)
var backpackMenuItemPurchasePrice = 3250
var backpackMenuItemPurchaseCapacity = 500
var backpackMenuItemPurchase = format.item(items.of(items.stackEss("chest 1")).name(colorize("&6&l[!] &ePurchase Backpack")).loreSet([
	colorize("&7&o\"Extra inventory inside of a backpack.\""),
	"",
	colorize("&6&l * &eCost: &6%price% Tokens"),
	colorize("&6&l * &eCapacity: &6%capacity% Items"),
]).commit())
var backpackMenuItemUpgradePrice = 7
var backpackMenuItemUpgradeBaseCapacity = 50
var backpackMenuItemUpgrade = format.item(items.of(items.stackEss("enderchest 1")).name(colorize("&6&l[!] &eUpgrade Backpack")).loreSet([
	colorize("&7&o\"Upgrade capacity of your current backpack.\""),
	"",
	colorize("&6&l * &ePrice: &6x%price% Tokens &7/ Item"),
	colorize("&7&o(( Left click to buy &bx%basecapacity%&3 (%price% Tokens)&7&o ))"),
	colorize("&7&o(( Right click to buy &a<X>&7&o ))"),
]).commit())
var backpackMenuItemMerge = items.of(items.stackEss("anvil 1")).name(colorize("&6&l[!] &eMerge All Backpacks")).loreSet([
	colorize("&7&o\"Merge all backpacks into one single backpack!\""),
]).commit()

exports.put("tokenshoputil", {
	internal: () => enchants,
	enchantEnsure: isItemEnchantable,
})

function showMainMenu(player) {
	var playerEXP = new ExpUtils(player)
	var menu = menus.newBuilder("chest")
	menu.newPartition(mainMenuSchemeMask, mainMenuScheme)
	menu.newPartition(mainMenuMask, partition => {
		var mainMenuWithdraw = mainMenuWithdrawItem.clone()
		var mainMenuEXP = mainMenuEXPItem.clone()
		itemName(mainMenuWithdraw, colorize("&e&l[!]&6 You have &f" + formatCommas(tokensutil().get(player)) + " &6Tokens"))
		itemName(mainMenuEXP, colorize("&e&l[!]&6 You have &f" + formatCommas(playerEXP.getCurrentExp()) + " &6EXP"))
		//partition.newSlot(mainMenuExplosiveItem, () => showLegacyGUI(player, GUI.getInstance().explosives(), () => showMainMenu(player)))
		itemSkullSetOwner(mainMenuPassiveItem, player.getName())
		partition.newSlot(mainMenuPassiveItem, () => showPassiveShop(player, () => showMainMenu(player)))
		partition.newSlot(mainMenuEXP, () => showEXPShop(player, () => showMainMenu(player)))
		partition.newSlot(mainMenuToolItem, () => showEnchantShop(player, () => showMainMenu(player)))
		partition.newSlot(mainMenuWithdraw, () => showWithdraw(player, () => showMainMenu(player)))
		partition.newSlot(mainMenuBackpackItem, () => showBackpackShop(player, () => showMainMenu(player)))
	})
	menus.openTitledMenu(player, menu, colorize("&8&lTOKEN SHOP"))
}

function showPassiveShop(player, closeCallback){
	if (closeCallback == undefined) {
		closeCallback = null
	}
	var menu = menus.newBuilder("chest")
	menu.setCloseCallback(closeCallback)
	menu.newPartition(passiveMenuMask, partition => {
		abstractPassivesEnchants.forEach(enchant => {
			var item = enchant.icon.clone()
			if(item.getType() == Material.SKULL_ITEM){
				item = itemStack("skull item", 1, 3);
				itemSkullSetOwner(item, player.getName())
			}
			itemGlow(item)
			itemName(item, colorize("&e&l[!]&6 " + enchant.title))
			for (var i in enchant.lore) {
				itemAddLore(item, colorize(enchant.lore[i]))
			}
			var tokenString = " &cTOKENS";
			if(enchant.token == "ptoken"){ tokenString = " &4PRESTIGE TOKENS" }
			itemAddLore(item, colorize("Cost Type: " + tokenString) )
			itemAddLore(item, colorize("&6&l * &ePrice: &f" + formatCommas(Math.ceil(abstractDiscountsMultiplier(player) * enchant.price))))

			var passiveItem = getPassiveItem(player);
			if(passiveItem){
				var passiveLeveL = passiveLevel(player, enchant.key)
				if(enchant.key == "blessing"){
					itemAddLore(item, colorize("&6&l * &b*** You already own a Blessing Container ***"))
				} else {
					if(passiveLevel(player, enchant.key) <= 0){
						itemAddLore(item, colorize("&6&l * &b*** Purchase to unlock ***"))
						itemAddLore(item, colorize("&6&l * &rLevel: &f" + 
							"LOCKED" + "/" + formatCommas(enchant.levelMax)))
					} else {
						itemAddLore(item, colorize("&6&l * &rLevel: &f" + 
							formatCommas(passiveLevel(player, enchant.key)) + "/" + formatCommas(enchant.levelMax)))
					}
					itemAddLore(item, colorize("&6&l * &rLeft click to upgrade one time &f"))
					itemAddLore(item, colorize("&6&l * &rRight click to upgrade many times at once &f"))
				}
			} else {
				if(item.key != "blessing") {
					itemAddLore(item, colorize("&6&l * &rYou need a Blessing Container to unlock blessing upgrades."))
				}
			}
			partition.newSlot(item, (arg1, arg2, arg3, clickType) => {
				if (ClickType.LEFT == clickType) {
					enchant.onBuy(player, enchant, 1);
					showPassiveShop(player, closeCallback);
				} else if (ClickType.RIGHT == clickType) { 
					openPassiveSign(player, enchant, () => showPassiveShop(player, closeCallback));
				}
				return;
			});
		})
		var tokenItem = mainMenuWithdrawItem.clone()
		itemName(tokenItem, colorize("&e&l[!]&6 You have &f" + formatCommas(tokensutil().get(player)) + " &6Tokens"))
		var ptokenItem = mainMenuWithdrawItem.clone()
		itemName(ptokenItem, colorize("&e&l[!]&6 You have &f" + formatCommas(prestigetokensutil().get(player)) + " &cPrestige Tokens"))
		partition.newSlot(tokenItem, () => {})
		partition.newSlot(ptokenItem, () => {})
	})
	menu.newPartition(passiveMenuSchemeMask, passiveMenuScheme)
	menus.openTitledMenu(player, menu, colorize("&8&lPURCHASE BLESSINGS"))
}

function showEnchantShop(player, closeCallback, removeOnly) {
	if (closeCallback == undefined) {
		closeCallback = null
	}
	var menu = menus.newBuilder("chest")
	menu.setCloseCallback(closeCallback)
	menu.newPartition(enchantMenuMask, partition => {
		enchants.forEach(enchant => {
			var item = enchant.icon.clone()
			itemGlow(item)
			if (removeOnly) {
				itemName(item, colorize("&4&l[!]&c Remove &e" + enchant.title))
			} else {
				itemName(item, colorize("&e&l[!]&6 " + enchant.title))
			}
			for (var i in enchant.lore) {
				itemAddLore(item, colorize(enchant.lore[i]))
			}
			if (removeOnly) {
				itemAddLore(item, "")
				itemAddLore(item, colorize("&7&o(( Click to &c&nremove enchant&7&o from a tool / item! ))"))
			} else {
				itemAddLore(item, colorize("&6&l * &ePrice: &f" + formatCommas(Math.ceil(abstractDiscountsMultiplier(player) * enchant.price)) + " Tokens"))
				itemAddLore(item, colorize("&6&l * &eMax Level: &f" + formatCommas(enchant.levelMax)))
			}
			partition.newSlot(item, chooseEnchant(player, enchant, () => showEnchantShop(player, closeCallback, removeOnly), removeOnly))
		})
		if (removeOnly) {
			partition.newSlot(enchantUpgradeItem, () => showEnchantShop(player, () => showMainMenu(player)))
		} else {
			partition.newSlot(enchantDowngradeItem, () => showEnchantShop(player, () => showEnchantShop(player, closeCallback), true))
		}
	})
	menu.newPartition(enchantMenuSchemeMask, removeOnly && enchantDowngradeMenuScheme || enchantMenuScheme)
	menus.openTitledMenu(player, menu, colorize(removeOnly && "&8&lENCHANT REMOVAL" || "&8&lENCHANTS"))
}

function showEXPShop(player, closeCallback) {
	if (closeCallback == undefined) {
		closeCallback = null
	}
	var playerEXP = new ExpUtils(player)
	var transact = amount => {
		var exp = playerEXP.getCurrentExp()
		if (!exp || !amount) {
			player.sendMessage(colorize("&4&l[!] &cYou don't have any experience!"))
			closeCallback()
			return
		}
		var expPrice = amount * expConvertPrice
		if (exp < expPrice) {
			player.sendMessage(colorize("&4&l[!] &cYou don't have enough experience!"))
			if (amount > 1) {
				menus.refreshMenus(player)
			} else {
				closeCallback()
			}
			return
		}
		playerEXP.changeExp(-expPrice)
		player.sendMessage(colorize("&2&l[!] &aExchanged " + formatCommas(expPrice) + " Experience for +" + formatCommas(amount) + " Tokens"))
		tokensutil().give(player, amount)
		exp = playerEXP.getCurrentExp()
		if (exp < expConvertPrice) {
			closeCallback()
		} else {
			menus.refreshMenus(player)
		}
	}
	var menu = menus.newBuilder("chest")
	menu.setCloseCallback(closeCallback)
	menu.newPartition(expMenuSchemeMask, expMenuScheme)
	menu.newPartition(expMenuMask, partition => {
		var exp = playerEXP.getCurrentExp()
		var expConvertAllAmount = Math.floor(exp / expConvertPrice)
		var expConvertAllPrice = expConvertAllAmount * expConvertPrice
		var expConvertAll = expConvertAllItem.clone()
		itemAddLore(expConvertAll, colorize("&6&l * &ePrice: &f" + formatCommas(expConvertAllPrice) + " Experience"))
		itemAddLore(expConvertAll, colorize("&6&l * &eExchange: &f+" + formatCommas(expConvertAllAmount) + " Tokens"))
		partition.newSlot(expConvertOneItem, () => transact(1))
		partition.newSlot(expConvertAll, () => transact(expConvertAllAmount))
	})
	menus.openTitledMenu(player, menu, colorize("&8&lEXP EXCHANGE"))
}

function showBackpackShop(player, closeCallback) {
	if (closeCallback == undefined) {
		closeCallback = null
	}
	var menu = menus.newBuilder("chest")
	menu.setCloseCallback(closeCallback)
	menu.newPartition(backpackMenuSchemeMask, backpackMenuScheme)
	menu.newPartition(backpackMenuMask, partition => {
		var purchaseUpgrade = (upgradeCapacity) => {
			if (purchaseRatelimit(player)) {
				return
			}
			showBackpackShop(player, closeCallback)
			var upgradeCapacityMax = Math.floor(tokensutil().get(player) / 
				Math.ceil(abstractDiscountsMultiplier(player) * backpackMenuItemUpgradePrice))
			if (upgradeCapacityMax == 0) {
				player.sendMessage(colorize("&4&l[!] &cYou don't have enough tokens!"))
				return
			}
			if (upgradeCapacity > upgradeCapacityMax) {
				upgradeCapacity = upgradeCapacityMax
			}
			var itemInHand = player.getItemInHand()
			if (!itemInHand || !BackpackManager.getInstance().isBackpack(itemInHand)) {
				player.sendMessage(colorize("&4&l[!] &cYou need to be holding a Backpack in your hand."))
				return
			}
			var upgradePrice = (Math.ceil(abstractDiscountsMultiplier(player) * (backpackMenuItemUpgradePrice * upgradeCapacity)))
			tokensutil().take(player, upgradePrice, rollback => {
				itemInHand = player.getItemInHand()
				if (!BackpackManager.getInstance().isBackpack(itemInHand)) {
					player.sendMessage(colorize("&4&l[!] &cYou need to be holding a Backpack in your hand."))
					rollback()
					return
				}

				//let upgradedBackpack = BackpackUtils.upgradeBackpack(itemInHand, upgradeCapacity)
				var upgradedBackpack = new Backpack(itemInHand)
				upgradedBackpack.setMaxItems(upgradedBackpack.getMaxItems() + upgradeCapacity)
				let backpackItem = upgradedBackpack.getBackpack()
				log(`upgraded to: ${upgradedBackpack}`)
				player.setItemInHand(backpackItem)
				player.updateInventory();
				player.sendMessage(colorize("&6&l[!] &eAdded &fx" + format.commas(upgradeCapacity) + 
					" &eto your &6Backpack&e for &a" + tokensSymbol + format.money(upgradePrice) + " Tokens"))
			})
		}
		var purchasePrice = backpackMenuItemPurchasePrice
		var purchaseCapacity = backpackMenuItemPurchaseCapacity
		partition.newSlot(backpackMenuItemPurchase.format({
			price: format.commas(purchasePrice),
			capacity: format.commas(purchaseCapacity),
		}), () => {
			if (!tokensutil().has(player, purchasePrice)) {
				player.sendMessage(colorize("&4&l[!] &cYou don't have enough tokens!"))
				return
			}
			if (purchaseRatelimit(player)) {
				return
			}
			tokensutil().take(player, purchasePrice, () => {
				var purchaseItem = new Backpack(purchaseCapacity).getBackpack();
				claimutil().givePlayer(player, purchaseItem, "Token Shop")
				player.sendMessage(colorize("&6&l[!] &ePurchased &fx" + format.commas(purchaseCapacity) + 
					" &6Backpack&e for &a" + tokensSymbol + format.money(purchasePrice) + " Tokens"))
			})
		})
		partition.newSlot(backpackMenuItemUpgrade.format({
			price: format.commas(backpackMenuItemUpgradePrice),
			basecapacity: format.commas(backpackMenuItemUpgradeBaseCapacity),
			baseprice: format.commas(backpackMenuItemUpgradePrice * backpackMenuItemUpgradeBaseCapacity),
		}), (a1, a2, a3, clickType) => {
			if (clickType == ClickType.LEFT) {
				purchaseUpgrade(backpackMenuItemUpgradeBaseCapacity)
			} else {
				menus.openSign(player, ["", "^ ^ ^", "Enter amount"], lines => {
					showBackpackShop(player, closeCallback)
					try {
						var amount = Integer.parseInt(lines[0])
						if (amount < 1) {
							throw "lol"
						}
						purchaseUpgrade(amount)
					} catch (e) {
						player.sendMessage(colorize("&4&l[!] &cYou need to enter a number!"))
					}
				})
			}
		})
		partition.newSlot(backpackMenuItemMerge, () => {
			var backpackMerge = 0
			var backpackMergeTotal = 0
			var inventoryContents = player.getInventory().getContents()
			for (var contentIdx in inventoryContents) {
				var content = inventoryContents[contentIdx]
				if (!BackpackManager.getInstance().isBackpack(content)) {
					continue
				}
				backpackMerge += new Backpack(content).getMaxItems()
				backpackMergeTotal++
				inventoryContents[contentIdx] = null
			}
			if (!backpackMerge || backpackMergeTotal < 2) {
				player.sendMessage(colorize("&4&l[!] &cYou don't have any backpacks to merge."))
				return
			}
			player.getInventory().setContents(inventoryContents)
			player.getInventory().addItem(new Backpack(backpackMerge).getBackpack())
			player.sendMessage(colorize("&2&l[!] &aYou have &cmerged all&a into a &esingle backpack&a. &7&o(( &f&o" + format.commas(backpackMerge) + "&7&ox ))"))
		})
	})
	menus.openTitledMenu(player, menu, colorize("&8&lBACKPACKS"))
}

function showWithdraw(player, closeCallback) {
	menus.openSign(player, ["", "^ ^ ^", "Enter amount"], lines => {
		try {
			var amount = Integer.parseInt(lines[0])
			if (amount < 1) {
				throw "lol"
			}
			bukkit.dispatchCommand(player, "tokens withdraw605275ur45m2d0w " + amount)
		} catch (e) {
			player.sendMessage(colorize("&4&l[!] &cYou need to enter a number!"))
		}
		if (closeCallback) {
			closeCallback()
		}
	})
}

function showLegacyGUI(player, gui, closeCallback) {
	if (gui) {
		menuForceClose(player)
		gui.openMenu(player)
		if (closeCallback) {
			gui.setMenuCloseBehaviour(() => {
				bukkit.runSync(() => {
					closeCallback()
				})
			})
		}
		return true
	}
	return false
}

function openPassiveSign(player, enchant, closeCallback){
	if(hasPassiveItem(player)) {
		menus.openSign(player, ["", "^ ^ ^", "Enter amount"], lines => {
			try {
				var amount = Integer.parseInt(lines[0])
				if (amount < 1) {
					throw "lol"
				}
				log("testing amount " + amount)
				if(amount instanceof Integer) {
					enchant.onBuy(player, enchant, amount)
				}
				closeCallback();
			} catch (e) {
				e.printStackTrace();
				player.sendMessage(colorize("&4&l[!] &cYou need to enter a number!"))
			}
			if (closeCallback) {
				closeCallback()
			}
		})
		
	}
}

function chooseEnchant(player, enchant, closeCallback, removeOnly) {
	return () => {
		if (enchant.comingSoon && !player.isOp()) {
			player.sendMessage(colorize("&4&l[!] &cThis enchant is coming soon!"))
			return
		}
		if (!removeOnly && !tokensutil().has(player, Math.ceil(enchant.price * abstractDiscountsMultiplier(player)))) {
			player.sendMessage(colorize("&4&l[!] &cYou don't have enough tokens!"))
			return
		}
		var enchantItemCheck = isItemEnchantable(enchant, removeOnly)
		var pickTitle = colorize("&8&lENCHANTS » " + enchant.title)
		if (removeOnly) {
			pickTitle = colorize("&8&lREMOVE » " + enchant.title)
		}
		pickItemForUpgrade(player, pickTitle, enchantItemCheck, 1, closeCallback, (slotId, n) => {
			var inventory = player.getInventory()
			var slotItem = inventory.getItem(slotId)
			if (purchaseRatelimit(player)) {
				return
			}
			if (!enchantItemCheck(slotItem)) {
				player.sendMessage(colorize("&4&l[!] &cIt doesn't look like you picked a compatible tool!"))
				return
			}
			if (!enchant.onBuy) {
				player.sendMessage(colorize("&4&l[!] &cYou can't buy this enchant!"))
				return
			}

			let enchantMax = false
			let price = 0

			if(n === "max") {
				enchantMax = true
				let playerTokens = tokensutil().get(player)
				n = Math.floor(playerTokens / Math.ceil(enchant.price * abstractDiscountsMultiplier(player)))
				n = Math.min(n, enchant.levelMax)
			} else {
				price = enchant.price * n
			}

			if (removeOnly) {
				if (!enchant.onBuy(slotItem, 0, enchant.levelMax, false, true)) {
					player.sendMessage(colorize("&4&l[!] &cIt doesn't look like you picked a compatible tool!"))
				} else {
					player.sendMessage(colorize("&6&l[!] &e" + enchant.title + " has been removed from tool! &7&o\"Disenchant!\""))
				}
				return
			} else {
				if(enchantMax) {
					let currentLevel = enchant.onBuy(slotItem, 0, enchant.levelMax, true)
					let levelsUntilMax = enchant.levelMax - currentLevel
					n = Math.min(n, levelsUntilMax)
					price = Math.ceil(abstractDiscountsMultiplier(player) * enchant.price) * n
					log(`current level: ${currentLevel}, levels to max: ${levelsUntilMax}, buying: ${n}`)
				}
				if (!tokensutil().has(player, price)) {
					player.sendMessage(colorize("&4&l[!] &cYou don't have enough tokens!"))
					return
				}
				if (!enchant.onBuy(slotItem, n, enchant.levelMax, true)) {
					player.sendMessage(colorize("&4&l[!] &cYou can't upgrade this enchant that much! &7(" + formatCommas(enchant.levelMax) + " max level)"))
					return
				}
				var priceMod = doDiscount(player, inventory.getBoots())
				if (priceMod < 1) {
					price = Math.floor(price * priceMod)
					player.sendMessage(colorize("&6&l ~ &e&lDISCOUNT DENIS &7&o\"&c&l-" + formatCommas((1 - priceMod) * 100) + "% OFF&7&o\""))
				}
				price = Math.ceil(abstractDiscountsMultiplier(player) * price)
				tokensutil().take(player, price, () => {
					var finalPrice = price
					slotItem = player.getInventory().getItem(slotId)
					if (!enchantItemCheck(slotItem)) {
						player.sendMessage(colorize("&4&l[!] &cIt doesn't look like you picked a compatible tool!"))
						tokensutil().give(player, finalPrice)
						return
					}
					if (!enchant.onBuy(slotItem, n, enchant.levelMax)) {
						player.sendMessage(colorize("&4&l[!] &cYou can't upgrade this enchant that much! &7(" + formatCommas(enchant.levelMax) + " max level)"))
						tokensutil().give(player, finalPrice)
						return
					}
					player.getInventory().setItem(slotId, slotItem)
					player.sendMessage(colorize("&6&l[!] &eUpgraded " + n + "x " + 
						colorize(enchant.title) + " for &a" + tokensSymbol + 
							formatCommas(finalPrice) + " Tokens"))
				})
			}
		})
	}
}

function checkRedItemsLoaded(){
	if(!RedItems || !absUtils || !AbstractImbues || !AbstractPassives){
		RedItems = pluginGet("RedItems")
		RedItems = RedItems && RedItems
		absUtils = pluginGet("AbstractEnchantsOP")
		AbstractImbues = absUtils && absUtils.imbuedController();
		AbstractPassives = absUtils && absUtils.passivesController();
	}
	return (RedItems != null && absUtils != null && AbstractImbues != null && AbstractPassives != null)
}

function hasPassiveItem(player) {
	var item = AbstractPassives.getPassiveItem(player); 
	if (!item) {
		return false
	}
	return true
}

function getPassiveItem(player) {
	return AbstractPassives.getPassiveItem(player);
}

function abstractDiscountsMultiplier(player){
	var mult = 1;
	mult -= getKickdownMultiplier(player)
	mult -= cloutMultiplier(player)
	return mult;
}

function abstractDiscountsMultiplierOverOne(player){
	var mult = 1;
	mult += getKickdownMultiplier(player)
	mult += cloutMultiplier(player)
	return mult;
}

function getKickdownMultiplier(player){
	var kickDown = 0;
	if(checkRedItemsLoaded()) {
		if(AbstractImbues.getEffectData(player, "kickdown")){
			kickDown = AbstractImbues.getImbuedData("kickdown").getPassivesBonuses()[0]
		}
	}
	return kickDown;
}

function cloutMultiplier(player){
	var clout = 0;
	if(checkRedItemsLoaded() && hasPassiveItem(player)){
		var cloutLevel = Math.max(0, passiveLevel(player, "clout"));
		var cloutMultTemp = AbstractPassives.getPassiveData("clout").getChancePerLevel();
		clout = cloutLevel * cloutMultTemp
	}
	return clout;
}

function doDiscount(player, item) {
	
	//var kickdownPantsMult = getKickdownMultiplier(player);
	var enchUtil = enchutil()
	var priceMod = 1
	var discountDenis = enchUtil && enchUtil.getByName("discount_denis")
	var discountDenisLvl = discountDenis && enchUtil.getLvl(item, discountDenis)
	if (!discountDenisLvl) return priceMod

	var cooldown = discountDenis.orbCooldown
	if (!cooldown || cooldown.ensure(player, item)) {
		var percentDiscount = Math.min(2 + Math.floor((discountDenisLvl / 5) * 23), 25) / 100
		priceMod -= percentDiscount
	}
	// Ensure kickdownpants multiplier.
	//priceMod *= abstractDiscountsMultiplierOverOne(player)
	return priceMod
}

function doEnchant(enchantment) {
	return (item, count, cap, readOnly, remove) => {
		if (readOnly === undefined) {
			readOnly = false
		}
		if (remove === undefined) {
			remove = false
		}
		if (count > cap || count > enchantNmsCap) {
			return false
		}
		if (item.containsEnchantment(enchantment)) {
			count += item.getEnchantmentLevel(enchantment)
			if (count > cap || count > enchantNmsCap && readOnly) {
				if(remove)
					return true
				return false
			}
			if (!readOnly) {
				if (remove) {
					item.removeEnchantment(enchantment)
				} else {
					item.addUnsafeEnchantment(enchantment, count)
				}
			}
		} else {
			if (!readOnly && !remove) {
				item.addUnsafeEnchantment(enchantment, count)
			}
		}
		return count
	}
}


function addPassiveItem() {
	return (player, enchant, amount) => {
		if(!getPassiveItem(player)) {
			player.sendMessage("Blessing Container purchased!")
			bukkit.dispatchCommand("passive start " + player.getName());
		} else {
			player.sendMessage("If you want a new container, put yours into storage.")
		}
	}
}
 
function canLevelPassive(player, enchant, amount)
{
	return (Math.max(0, passiveLevel(player, enchant.key)) + amount <= enchant.levelMax)
}

function passiveLevel(player, passiveKey) {
	return AbstractPassives.getPassiveLevel(AbstractPassives.getPassiveItem(player), passiveKey)
}

function levelPassive() {
 	return (player, enchant, amount) => {
		if (enchant.comingSoon && !player.isOp()) {
			player.sendMessage(colorize("&4&l[!] &cThis blessing is coming soon!"))
			return
		}
		var cost = Math.ceil((enchant.price * abstractDiscountsMultiplier(player)) * amount)
		log("Amount check: " + amount)
		log("enchant max level check: " + enchant.levelMax)
		if(amount + passiveLevel(player, enchant.key) > enchant.levelMax) {
			player.sendMessage(colorize("&4&l[!] &cYou set the amount too high, you cannot upgrade this much."))
			return;
		}
		cost = Math.ceil((enchant.price * abstractDiscountsMultiplier(player)) * amount)
		if (enchant.token == "token" && !tokensutil().has(player, cost)) {
			player.sendMessage(colorize("&4&l[!] &cYou don't have enough tokens to upgrade this blessing " + amount + " times."))
			player.sendMessage(colorize("&4&l[!] &cYou only have  " + formatCommas(tokensutil().get(player)) + "/" + formatCommas(cost) + " tokens." ))
			return
		}
		else if (enchant.token == "ptoken" && !prestigetokensutil().has(player, cost)) {
			player.sendMessage(colorize("&4&l[!] &cYou don't have enough prestige tokens to upgrade this blessing " + amount + " times."))
			player.sendMessage(colorize("&4&l[!] &cYou only have  " + formatCommas(prestigetokensutil().get(player)) + "/" + formatCommas(cost) + " prestige tokens." ))
			return
		}
		var passiveItem = getPassiveItem(player)
		if(passiveItem){
			log("debug amount check " + amount);
			log("debug amount: " + amount + " " + enchant)
			if(canLevelPassive(player, enchant, amount)) {
				var passiveCheck = passiveLevel(player, enchant.key)
				if(!AbstractPassives.canLevelPassive(passiveItem, enchant.key, amount)) {
					if(passiveLevel(player, enchant.key) != passiveCheck + amount) {
						player.sendMessage("There was an internal error, your tokens will not be charged, try again.")
						return;
					}
				}
				else
				{
					AbstractPassives.addPassiveLevel(player, enchant.key, amount);
				}
				if(enchant.token == "token")
					tokensutil().take(player, cost)
				else if (enchant.token == "ptoken")
					prestigetokensutil().take(player, cost)
				player.sendMessage(colorize("Upgraded blessing " + enchant.title + " to level &c" + passiveLevel(player, enchant.key)))
			} else {
				player.sendMessage("Blessing cannot be leveled to that level.")
			}
		} else {
			player.sendMessage("You need a blessing container to level blessings.")
		}
 	}
}

function doLoreEnchant(lore) {
	lore = colorize("&7") + lore + " "
	return (item, count, cap, readOnly, remove) => {
		if (readOnly === undefined) {
			readOnly = false
		}
		if (remove === undefined) {
			remove = false
		}
		var level = count
		if (level > cap && !remove) {
			return false
		}
		var itemLore = item.getItemMeta().getLore()
		if (itemLore == null) {
			if (!readOnly && !remove) {
				itemAddLore(item, lore + level)
			}
			return level
		}
		for (var i in itemLore) {
			if (!itemLore[i].startsWith(lore)) {
				continue
			}
			level += Integer.parseInt(itemLore[i].substring(lore.length()))
			if (level > cap && readOnly) {
				if(remove)
					return true;
				return false
			}
			if (!readOnly) {
				if (remove) {
					itemLore.remove(i)
				} else {
					itemLore[i] = lore + level
				}
				var itemMeta = item.getItemMeta()
				itemMeta.setLore(itemLore)
				item.setItemMeta(itemMeta)
			}
			return level
		}
		if (!readOnly && !remove) {
			itemAddLore(item, lore + level)
		}
		return level
	}
}

function pickItemForUpgrade(player, title, isX, n, closeCallback, clickCallback) {
	menuPickItem(player, title, isX, item => {
		itemAddLore(item, "")
		itemAddLore(item, colorize("&7&o(( Left click for " + n + "x ))"))
		itemAddLore(item, colorize("&7&o(( Middle click for max ))"))
		itemAddLore(item, colorize("&7&o(( Right click for <x> amount ))"))
	}, (i, clickType) => {
		if (clickType == ClickType.RIGHT) {
			menus.openSign(player, ["", "^ ^ ^", "Enter amount"], lines => {
				try {
					var amount = Integer.parseInt(lines[0])
					if (amount < 1) {
						throw "lol"
					}
					clickCallback(i, amount)
				} catch (e) {
					player.sendMessage(colorize("&4&l[!] &cYou need to enter a number!"))
				}
				closeCallback()
			})
		} else if(clickType === ClickType.MIDDLE) {
			clickCallback(i, "max")
		} else {
			clickCallback(i, 1)
		}
	}, closeCallback)
}

function isItemEnchantable(enchant, remove) {
	return item => {
		if (!item) {
			return false
		}
		if (remove && !enchant.onBuy(item, 0, enchant.levelMax, true, true)) {
			return false
		}
		return itemIsPickaxe(item) || itemIsAxe(item) || itemIsShovel(item) || (enchant.allowWeapon && itemIsSword(item))
	}
}

function purchaseRatelimit(player) {
	var attributes = bukkit.getAttributes(player)
	var lastPurchase = attributes.get(purchaseRatelimitAttribute)
	if (lastPurchase && new Date().getTime() - lastPurchase < 100) {
		return true
	}
	attributes.put(purchaseRatelimitAttribute, new Date().getTime())
	return false
}

bukkit.aliasCommand("enchant", "tokenshop")
bukkit.aliasCommand("enchants", "tokenshop")
bukkit.aliasCommand("tshop", "tokenshop")
bukkit.registerCommand("tokenshop", false, showMainMenu)
