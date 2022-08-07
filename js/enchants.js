load("util:items.js")
load("util:plugins.js")
load("util:sounds.js")
load("next:extern/format.js")

importClass("org.pvpingmc.tokens.Main")

var enchutil = exports.ptr("setsenchantsutil")

var mrlInstance = pluginGet("MineResetLite")

var enchantPrefix = colorize("&7")
var enchantDisabledWorlds = new HashSet()

var RedItems = pluginGet("RedItems")
var RedItems = RedItems && RedItems
var absUtils = pluginGet("AbstractEnchantsOP")
var AbstractImbues = absUtils && absUtils.imbuedController();
var AbstractPassives = absUtils && absUtils.passivesController();

bukkit.runSync(() => {
	enchantDisabledWorlds.addAll(Main.getInstance().getDisableWorlds())
})

function canUseEnchants(player) {
	return player.getGameMode() != GameMode.CREATIVE
		&& !enchantDisabledWorlds.contains(player.getWorld().getName())
}

function getMineManager() {
	return mrlInstance && mrlInstance.getMineManager()
}

function getEnchantLevel(player, enchantName) {
	if (!canUseEnchants(player)) {
		return 0
	}
	var item = player.getItemInHand()
	if (item == null || !itemIsTool(item) || !item.hasItemMeta()) {
		return 0
	}
	var itemMeta = item.getItemMeta()
	if (!itemMeta.hasLore()) {
		return 0
	}
	var loreEnchant = enchantPrefix + enchantName + " "
	var lores = itemMeta.getLore()
	var lore
	for (var i in lores) {
		lore = lores[i]
		if (lore.startsWith(loreEnchant)) {
			return Integer.parseInt(lore.substring(loreEnchant.length()))
		}
	}
	return 0
}

// Enchant - Beacon Blast & Set Enchant - Dragons Breath

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
function hasPassiveItem(player){
	var passiveItem = AbstractPassives.getPassiveItem(player); 
	return (passiveItem != null)
}

function getSpongeChance(passiveItem){
	var data = AbstractPassives.getPassiveData("spongeexchange")
	var fortune = Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "spongeexchange"));
	var mult = fortune * data.getChancePerLevel();
	var maxCount = data.getMaxChance();
	return [mult, maxCount];
}
function getBeaconChance(passiveItem){
	var data = AbstractPassives.getPassiveData("beaconexchange")
	var fortune = Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "beaconexchange"));
	var mult = fortune * data.getChancePerLevel();
	var maxCount = data.getMaxChance();
	return [mult, maxCount];
}
function getDragonEggChance(passiveItem){
	var data = AbstractPassives.getPassiveData("dragoneggexchange")
	var fortune = Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "dragoneggexchange"));
	var mult = fortune * data.getChancePerLevel();
	var maxCount = data.getMaxChance();
	return [mult, maxCount];
}
function getMagneticKeyChance(passiveItem){
	var data = AbstractPassives.getPassiveData("magneticfinder")
	var fortune = Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "magneticfinder"));
	var mult = fortune * data.getChancePerLevel();
	var maxCount = data.getMaxChance();
	return [mult, maxCount];
}
function getScratchersAhoyChance(passiveItem){
	var data = AbstractPassives.getPassiveData("scratchersahoy")
	var fortune = Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "scratchersahoy"));
	var mult = fortune * data.getChancePerLevel();
	var maxCount = data.getMaxChance();
	return [mult, maxCount];
}

function getPassiveTrainerChance(passiveItem){
	var data = AbstractPassives.getPassiveData("passivetrainer")
	var fortune = Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "passivetrainer"));
	var mult = fortune * data.getChancePerLevel();
	var maxCount = data.getMaxChance();
	return [mult, maxCount];
}

function getTrinketsMultiplier(player, index){
	var mult = 0;
	if(checkRedItemsLoaded()) {
		var trinketData = AbstractImbues.isWearingImbue(player, "trinket")
		if(trinketData) {
			mult = trinketData.getEffect().getArgs()[index]
		}
	}
	return mult;
}
function getTrinketsMultipliers(player){
	var mult = 1;
	if(checkRedItemsLoaded()) {
		var trinketData = AbstractImbues.isWearingImbue(player, "trinket")
		if(trinketData) {
			mult = 1.5
		}
	}
	return mult;
}

function getUniqueItemPouchChance(player) {
	var levels = 0;
	if(hasPassiveItem(player)){
		var passiveItem = AbstractPassives.getPassiveItem(player);
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "clout"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "beaconexchange"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "clusterbombs") * 1000);
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "dragoneggexchange"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "drilldozer") * 5000);
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "armageddon"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "demolitions"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "magneticfinder"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "scratchersahoy"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "spongeexchange"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "tokenbaron"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "veryveryfortunate") * 4);
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "veryfortunate"));
		levels += Math.max(0, AbstractPassives.getPassiveLevel(passiveItem, "passivetrainer") * 2);
	}
	return levels / 14.0
}

var dragonBreathChance = .0025
var beaconBlastChance = .000004

bukkit.registerEvent(BlockBreakEvent.class, event => {
	var player = event.getPlayer()
	var inventory = player.getInventory()
	var level = getEnchantLevel(player, "BeaconBlast")
	if (level) {
		var chance = beaconBlastChance * level
		if (Math.random() * 100 <= chance) {
			rewardBeaconBlast(player)
		}
	}
	level = checkDragonBreath(player, inventory.getChestplate())
	if (level) {
		var chance = dragonBreathChance * level
		if (Math.random() * 100 <= chance) {
			rewardDragonBreath(player)
		}
	}
	runEnchantsCheck(event, 1)
	//log(getUniqueItemPouchChance(player))
	if(ThreadLocalRandom.current().nextInt(1, 500000000) <= getUniqueItemPouchChance(player)){
		broadcastUniqueItemDrop(player);
	}
	internalKeyMaster(event.getPlayer(), event.getBlock(), inventory.getLeggings())
})

function broadcastUniqueItemDrop(player) {
	bukkit.dispatchCommand("claimgive " + player.getName() + " POUCH loot {} godlyitempouch 1")
	bukkit.getPlayers().forEach(otherPlayer => {
		if (player != null && otherPlayer != null && player == otherPlayer) {
			log("Broadcast to: " + player.getName());
			player.sendMessage(colorize("&6&l[!] &eYour &cBlessing Container's &6potency gave you"))
			player.sendMessage(colorize("&cVERY RARE &6UNIQUE ITEM POUCH! &cCONGRATULATIONS!!!"))
			player.sendMessage(colorize("&6&l[!] &eAll online players have been notified of your fortune!"))
		} else if(player != null && otherPlayer != null && otherPlayer instanceof Player) {
			log("Awarded unique item container: " + player.getName());
			otherPlayer.sendMessage(colorize("&6&l[!!!] &ePRISONS ANNOUNCEMENT: &c" + player.getName() + " &6 recieved a &cVERY RARE &6UNIQUE ITEM POUCH! Congratulate them!!!"))
		}
	})
}

importClass("org.pvpingmc.tokens.event.EnchantBlockBreakEvent")

bukkit.registerEvent(EnchantBlockBreakEvent.class, event => {
	var player = event.getPlayer()
	var level = getEnchantLevel(player, "BeaconBlast")
	if (level) {
		var chance = beaconBlastChance * level * event.getBlocksBroken()
		if (Math.random() * 100 <= chance) {
			rewardBeaconBlast(player)
		}
	}
	level = checkDragonBreath(player, player.getInventory().getChestplate())
	if (level) {
		var chance = dragonBreathChance * level * event.getBlocksBroken()
		if (Math.random() * 100 <= chance) {
			rewardDragonBreath(player)
		}
	}
	runEnchantsCheck(event, event.getBlocksBroken())
})



// Check chance for the individual things to proc.
function checkChance(chance_range_start, chance_ceiling){
	var rand = ThreadLocalRandom.current().nextInt(1, chance_ceiling)
	return (rand <= chance_range_start)
}

function runCheckChance(info, multiplier, amount){
	var baseValue = (info[0] * multiplier * Math.max(1, amount)) / 42000;
	return (checkChance(baseValue, info[1]))
}

function runEnchantsCheck(event, amount){
	var player = event.getPlayer();
	if(checkRedItemsLoaded()){
		var passiveItem = AbstractPassives.getPassiveItem(player);
		if(passiveItem != null){
			
			var spongeInfo = getSpongeChance(passiveItem)
			var beaconInfo = getBeaconChance(passiveItem)
			var dragonEggInfo = getDragonEggChance(passiveItem)
			var magneticInfo = getMagneticKeyChance(passiveItem)
			var scratcherInfo = getScratchersAhoyChance(passiveItem)
			var passiveTrainerInfo = getPassiveTrainerChance(passiveItem)
			var trinketMultipliers = getTrinketsMultipliers(player);
			if(runCheckChance(spongeInfo, trinketMultipliers, amount)) rewardSponge(player);
			if(runCheckChance(beaconInfo, trinketMultipliers, amount)) rewardBeacon(player);
			if(runCheckChance(dragonEggInfo, trinketMultipliers, amount)) rewardDragonEgg(player);
			if(runCheckChance(magneticInfo, trinketMultipliers, amount)) rewardKey(player);
			if(runCheckChance(scratcherInfo, trinketMultipliers, amount)) rewardScratcher(player);
			if(runCheckChance(passiveTrainerInfo, trinketMultipliers, amount)) rewardPassiveLevel(player);
			
		}
	}
}

function rewardBeaconBlast(player) {
	player.sendMessage(colorize("&6&l[!] &eYour &6Beacon Blast &eenchant spawned a beacon in your inventory or claim!"))
	bukkit.silentCommand("claimgive " + player.getName() + " BeaconBlast give {} beacon 1")
}

function rewardDragonBreath(player) {
	player.sendMessage(colorize("&6&l[!] &eYour &6Dragon Breath &eenchant spawned a dragon egg in your inventory or claim!"))
	bukkit.silentCommand("claimgive " + player.getName() + " DragonBreath give {} dragonegg 1")
}

function rewardBeacon(player) {
	player.sendMessage(colorize("&6&l[!] &eYour &6Divine blessing assisted with &espawning a beacon in your inventory or claim!"))
	bukkit.silentCommand("claimgive " + player.getName() + " BeaconExchange give {} beacon 1")
}

function rewardDragonEgg(player) {
	player.sendMessage(colorize("&6&l[!] &eYour &6Divine blessing assisted with &espawning a dragon egg in your inventory or claim!"))
	bukkit.silentCommand("claimgive " + player.getName() + " DragonEggExchange give {} dragonegg 1")
}

function rewardSponge(player) {
	player.sendMessage(colorize("&6&l[!] &eYour &6Divine blessing assisted with &espawning a sponge in your inventory or claim!"))
	bukkit.silentCommand("claimgive " + player.getName() + " SpongeExchange give {} sponge 1")
}

function rewardScratcher(player) {
	var luckyUtil = exports.ptr("luckycardsutil")
	var claimutil = exports.ptr("claimutil")
	player.sendMessage(colorize("&6&l[!] &eYour &6Divine blessing assisted with &espawning a scratcher in your inventory or claim!"))
	var cardEnt = luckyUtil().cards.get(Math.floor(ThreadLocalRandom.current().nextInt(0, luckyUtil().cards.size() - 1)));
	claimutil().givePlayer(player, cardEnt.loot, "Lucky Cards", true);
}

function rewardKey(player) {
	if (!keyMasterEnch) {
		keyMasterEnch = enchutil().getByName("key_master")
	}
	var keyLevel = (ThreadLocalRandom.current().nextInt(1, 3))
	var keyArr = keyMasterEnch.keyTypes && keyMasterEnch.keyTypes[keyLevel]
	if (!keyArr) return

	var keyNameArr = []
	for (var i in keyArr) {
		var keyName = keyArr[i]
		keyNameArr.push("1x " + keyName)
		bukkit.dispatchCommand(formatExtern.locale(crateCommandSyntax, { player: player.getName(), name: keyName }))
	}
	player.sendMessage(colorize("&5&l[!] &fYour &6divine blessing MAGNETIC &d has activated... &7&o\"KEY LEVEL " + keyMasterEnch.computeVariant(keyLevel) + " &7&o~ &b&o" + keyNameArr.join(", ") + "&7&o\""))
	soundKeyMaster(player)
}

function rewardPassiveLevel(player){
	player.sendMessage(colorize("&6&l[!] &eYour &6divine blessing PASSIVE TRAINER gave you an epiphany &eit increased the level of a random blessing!"))
	bukkit.silentCommand("passive addrandomlevel " + player.getName())
}

var dragonBreathEnch = null
var keyMasterEnch = null
var powerPetsEnch = null

function checkDragonBreath(player, item) {
	if (!enchutil()) return false
	if (!dragonBreathEnch) {
		dragonBreathEnch = enchutil().getByName("dragon_breath")
		return false
	}
	var dragonBreathLvl = enchutil().getLvl(item, dragonBreathEnch)
	if (!dragonBreathLvl) return false

	var cooldown = dragonBreathEnch.orbCooldown
	if (cooldown && !cooldown.ensure(player, item)) return false

	return dragonBreathLvl
}

// Set Enchant - Key Master

var crateCommandSyntax = "crate gk %player% %name% 1"
var soundKeyMaster = soundOf("FIREWORK_BLAST2;2.0;0.015")

function internalKeyMaster(player, block, item) {
	var mine = getMineManager().getByLocation(block.getLocation()) 
	if (!enchutil() || !mine) return
	if (!keyMasterEnch) {
		keyMasterEnch = enchutil().getByName("key_master")
		return
	}
	var keyMasterLvl = enchutil().getLvl(item, keyMasterEnch)
	if (!keyMasterLvl) return

	var cooldown = keyMasterEnch.orbCooldown
	if (cooldown && !cooldown.ensure(player, item)) return

	var procChance = (1 + keyMasterLvl) * .0045
	if (Math.random() * 100 <= procChance) {
		var keyArr = keyMasterEnch.keyTypes && keyMasterEnch.keyTypes[keyMasterLvl]
		if (!keyArr) return

		var keyNameArr = []
		for (var i in keyArr) {
			var keyName = keyArr[i]
			keyNameArr.push("1x " + keyName)
			bukkit.dispatchCommand(formatExtern.locale(crateCommandSyntax, { player: player.getName(), name: keyName }))
		}
		player.sendMessage(colorize("&5&l[!] &fKey Master&d has activated... &7&o\"LVL " + keyMasterEnch.computeVariant(keyMasterLvl) + " &7&o~ &b&o" + keyNameArr.join(", ") + "&7&o\""))
		soundKeyMaster(player)
	}
}


// Set Enchant - Power Pets

try {
	importClass("ninja.coelho.arkjs.extlib.event.PetExpGainEvent")

	bukkit.registerEvent(PetExpGainEvent.class, event => {
		if (!enchutil()) return
		if (!powerPetsEnch) {
			powerPetsEnch = enchutil().getByName("power_pets")
			return
		}
		var petModifiers = event.getPetModifiers()
		if (!petModifiers) return

		var player = event.getPlayer()
		var powerPetsLvl = enchutil().getCombinedLvl(player.getInventory().getArmorContents(), powerPetsEnch)
		if (!powerPetsLvl) return

		var expBuff = 1 + powerPetsLvl * .001
		petModifiers.expGain *= expBuff
	})
} catch(e) {
	log(e)
}

// Enchant - Lucky Duck

bukkit.runSync(() => {
	try {
		importClass("ninja.coelho.arkjs.extlib.event.ProcChanceEvent")

		var luckycardsutil = exports.ptr("luckycardsutil")

		bukkit.registerEvent(ProcChanceEvent.class, event => luckycardsutil().handle(event, () => {
			var player = event.getPlayer()
			var level = getEnchantLevel(player, "LuckyDuck")
			if (!level) {
				return
			}
			event.setOutOf(Math.max(event.getOutOf() * (1 - (level / 1500)), 1))
		}))
	} catch(e) {
		log(e)
	}
})

// Enchant - Double Trouble

try {
	importClass("org.pvpingmc.sell.events.PlayerSellEvent")
	
	bukkit.registerEvent(PlayerSellEvent.class, EventPriority.MONITOR, event => {
		if (event.getPrice() == 0) {
			return
		}
		var player = event.getPlayer()
		var level = getEnchantLevel(player, "DoubleTrouble")
		if (!level) {
			return
		}
		var chance = 0.05 * level
		if (Math.random() * 100 > chance) {
			return
		}
		var procBonus = 2
		var prependBonus = "Double"
		var enchUtil = enchutil()
		if (enchUtil) {
			var helmet = player.getInventory().getHelmet()
			var tripleTrouble = enchUtil.getByName("triple_trouble")
			var tripleTroubleLvl = tripleTrouble && enchUtil.getLvl(helmet, tripleTrouble)
			if (tripleTroubleLvl) {
				var cooldown = tripleTrouble.orbCooldown
				if (!cooldown || cooldown.ensure(player, helmet)) {
					procBonus = 3
					prependBonus = "Triple"
				}
			}
		}
		event.setItems(event.getItems() * procBonus)
		event.setPrice(event.getPrice() * procBonus)
		bukkit.runSync(() => player.sendMessage(colorize("&6&l[!] &eYour &6" + prependBonus + " Trouble &eenchant " + prependBonus.toLowerCase() + "d your blocks sold!")))
	})
} catch(e) {
	log(e)
}
