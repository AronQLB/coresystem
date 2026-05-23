package net.meetlounge.core;

import net.meetlounge.core.api.CoreAPI;
import net.meetlounge.core.auction.*;
import net.meetlounge.core.ban.*;
import net.meetlounge.core.bloodmoon.BloodMoonCommand;
import net.meetlounge.core.bloodmoon.BloodMoonListener;
import net.meetlounge.core.bloodmoon.BloodMoonService;
import net.meetlounge.core.chat.ChatListener;
import net.meetlounge.core.chat.ChatMuteCommand;
import net.meetlounge.core.chat.ClearChatCommand;
import net.meetlounge.core.chat.SlowChatCommand;
import net.meetlounge.core.clan.*;
import net.meetlounge.core.clan.claim.ClanClaimListener;
import net.meetlounge.core.clan.claim.ClanClaimRepository;
import net.meetlounge.core.clan.claim.ClanClaimService;
import net.meetlounge.core.clan.claim.ClanClaimVisualizer;
import net.meetlounge.core.combat.CombatListener;
import net.meetlounge.core.combat.CombatService;
import net.meetlounge.core.command.CommandManager;
import net.meetlounge.core.command.CoreCommand;
import net.meetlounge.core.config.ConfigManager;
import net.meetlounge.core.config.MessageManager;
import net.meetlounge.core.cooldown.CooldownService;
import net.meetlounge.core.database.DatabaseManager;
import net.meetlounge.core.debug.DebugLogger;
import net.meetlounge.core.economy.CoinsCommand;
import net.meetlounge.core.economy.EconomyService;
import net.meetlounge.core.economy.PayCommand;
import net.meetlounge.core.end.EndCommand;
import net.meetlounge.core.grave.GraveListener;
import net.meetlounge.core.home.*;
import net.meetlounge.core.image.ImageCommand;
import net.meetlounge.core.image.ImageService;
import net.meetlounge.core.language.PlaceholderService;
import net.meetlounge.core.level.LevelCommand;
import net.meetlounge.core.level.LevelListener;
import net.meetlounge.core.level.LevelService;
import net.meetlounge.core.listener.FirstJoinListener;
import net.meetlounge.core.listener.MotdListener;
import net.meetlounge.core.listener.PlayerConnectionListener;
import net.meetlounge.core.maintenance.MaintenanceService;
import net.meetlounge.core.module.ModuleManager;
import net.meetlounge.core.mute.MuteCommand;
import net.meetlounge.core.mute.MuteRepository;
import net.meetlounge.core.mute.MuteService;
import net.meetlounge.core.mute.UnmuteCommand;
import net.meetlounge.core.nether.NetherCommand;
import net.meetlounge.core.npc.NpcCommand;
import net.meetlounge.core.npc.NpcListener;
import net.meetlounge.core.npc.NpcRepository;
import net.meetlounge.core.npc.NpcService;
import net.meetlounge.core.permission.PermissionService;
import net.meetlounge.core.player.PlayerData;
import net.meetlounge.core.player.PlayerDataRepository;
import net.meetlounge.core.player.PlayerDataService;
import net.meetlounge.core.rank.RankCommand;
import net.meetlounge.core.rank.RankService;
import net.meetlounge.core.rank.RankTabCompleter;
import net.meetlounge.core.region.RegionCommand;
import net.meetlounge.core.region.RegionListener;
import net.meetlounge.core.region.RegionRepository;
import net.meetlounge.core.region.RegionService;
import net.meetlounge.core.registry.ServiceRegistry;
import net.meetlounge.core.report.*;
import net.meetlounge.core.rtp.RtpCommand;
import net.meetlounge.core.scheduler.AutoSaveService;
import net.meetlounge.core.scheduler.SchedulerService;
import net.meetlounge.core.spawn.*;
import net.meetlounge.core.staff.*;
import net.meetlounge.core.tab.CoreTabCompleter;
import net.meetlounge.core.teleport.TpAcceptCommand;
import net.meetlounge.core.teleport.TpaCommand;
import net.meetlounge.core.teleport.TpaService;
import net.meetlounge.core.visual.ChatFormatListener;
import net.meetlounge.core.visual.SidebarService;
import net.meetlounge.core.visual.TablistService;
import net.meetlounge.core.warp.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public final class Core extends JavaPlugin {

    public static String prefix = "&8[&aSMP&8] ";

    private static Core instance;

    private ServiceRegistry serviceRegistry;

    private ConfigManager configManager;
    private MessageManager messageManager;
    private DatabaseManager databaseManager;
    private PlayerDataService playerDataService;
    private PermissionService permissionService;
    private SchedulerService schedulerService;
    private AutoSaveService autoSaveService;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private CooldownService cooldownService;
    private EconomyService economyService;
    private RankService rankService;
    private PlaceholderService placeholderService;
    private MaintenanceService maintenanceService;
    private DebugLogger debugLogger;
    private CoreAPI api;
    private PlayerDataRepository playerDataRepository;
    private SidebarService sidebarService;
    private TablistService tablistService;
    private ClanRepository clanRepository;
    private ClanService clanService;
    private BanRepository banRepository;
    private BanService banService;
    private ReportRepository reportRepository;
    private ReportService reportService;
    private SpawnRepository spawnRepository;
    private SpawnService spawnService;
    private HomeRepository homeRepository;
    private HomeService homeService;
    private TpaService tpaService;
    private CombatService combatService;
    private WarpRepository warpRepository;
    private WarpService warpService;
    private MuteRepository muteRepository;
    private MuteService muteService;
    private RegionRepository regionRepository;
    private RegionService regionService;
    private ClanClaimRepository clanClaimRepository;
    private ClanClaimService clanClaimService;
    private ClanClaimVisualizer clanClaimVisualizer;
    private NpcRepository npcRepository;
    private NpcService npcService;
    private LevelService levelService;
    private VanishService vanishService;
    private AuctionRepository auctionRepository;
    private AuctionService auctionService;
    private ImageService imageService;
    private BloodMoonService bloodMoonService;

    private Map<UUID, PlayerData> playerDataCache;


    @Override
    public void onEnable() {
        instance = this;

        createServices();
        registerServices();

        configManager.load();
        messageManager.load();
        prefix = messageManager.prefix();

        databaseManager.connect();
        databaseManager.createTables();

        maintenanceService.load();
        spawnService.load();
        regionService.load();
        clanClaimService.load();
        npcService.load();

        playerDataService.loadOnlinePlayers();

        registerCommands();
        registerListeners();

        sidebarService.start();
        tablistService.start();
        levelService.startZombieSpawner();
        bloodMoonService.start();
        auctionService.startCleanupTask();

        moduleManager.enableModules();
        autoSaveService.start();


        debugLogger.info("SMP-Core erfolgreich gestartet.");
    }

    @Override
    public void onDisable() {
        if (autoSaveService != null) {
            autoSaveService.stop();
        }

        if (moduleManager != null) {
            moduleManager.disableModules();
        }

        if (playerDataService != null) {
            if (permissionService != null) {
                permissionService.clearAttachments();
            }
            playerDataService.shutdown();
        }

        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        if (serviceRegistry != null) {
            serviceRegistry.clear();
        }

        if (npcService != null) {
            npcService.despawnAll();
        }

        getLogger().info("SMP-Core erfolgreich gestoppt.");
    }

    private void createServices() {
        this.serviceRegistry = new ServiceRegistry();

        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.databaseManager = new DatabaseManager(this);

        this.permissionService = new PermissionService(this);
        this.schedulerService = new SchedulerService(this);
        this.autoSaveService = new AutoSaveService(this);

        this.playerDataRepository = new PlayerDataRepository(this);
        this.playerDataService = new PlayerDataService(this, playerDataRepository);

        this.moduleManager = new ModuleManager(this);
        this.commandManager = new CommandManager(this);
        this.cooldownService = new CooldownService();

        this.economyService = new EconomyService(this);
        this.rankService = new RankService(this);
        this.placeholderService = new PlaceholderService(this);
        this.maintenanceService = new MaintenanceService(this);
        this.debugLogger = new DebugLogger(this);

        this.sidebarService = new SidebarService(this);
        this.tablistService = new TablistService(this);

        this.clanRepository = new ClanRepository(this);
        this.clanService = new ClanService(this, clanRepository);

        this.banRepository = new BanRepository(this);
        this.banService = new BanService(this, banRepository);

        this.reportRepository = new ReportRepository(this);
        this.reportService = new ReportService(this, reportRepository);

        this.spawnRepository = new SpawnRepository(this);
        this.spawnService = new SpawnService(this, spawnRepository);

        this.homeRepository = new HomeRepository(this);
        this.homeService = new HomeService(homeRepository);

        this.tpaService = new TpaService();
        this.combatService = new CombatService();

        this.warpRepository = new WarpRepository(this);
        this.warpService = new WarpService(warpRepository);

        this.muteRepository = new MuteRepository(this);
        this.muteService = new MuteService(this, muteRepository);

        this.regionRepository = new RegionRepository(this);
        this.regionService = new RegionService(regionRepository);

        this.clanClaimRepository = new ClanClaimRepository(this);
        this.clanClaimService = new ClanClaimService(this, clanClaimRepository);
        this.clanClaimVisualizer = new ClanClaimVisualizer(this);

        this.npcRepository = new NpcRepository(this);
        this.npcService = new NpcService(this, npcRepository);

        this.levelService = new LevelService(this);
        this.vanishService = new VanishService(this);
        this.auctionRepository = new AuctionRepository(this);
        this.auctionService = new AuctionService(this, auctionRepository);
        this.imageService = new ImageService(this);
        this.bloodMoonService = new BloodMoonService(this);

        this.api = new CoreAPI(this);
    }

    private void registerServices() {
        serviceRegistry.register(ConfigManager.class, configManager);
        serviceRegistry.register(MessageManager.class, messageManager);
        serviceRegistry.register(DatabaseManager.class, databaseManager);
        serviceRegistry.register(PermissionService.class, permissionService);
        serviceRegistry.register(SchedulerService.class, schedulerService);
        serviceRegistry.register(AutoSaveService.class, autoSaveService);
        serviceRegistry.register(PlayerDataRepository.class, playerDataRepository);
        serviceRegistry.register(PlayerDataService.class, playerDataService);
        serviceRegistry.register(ModuleManager.class, moduleManager);
        serviceRegistry.register(CommandManager.class, commandManager);
        serviceRegistry.register(CooldownService.class, cooldownService);
        serviceRegistry.register(EconomyService.class, economyService);
        serviceRegistry.register(RankService.class, rankService);
        serviceRegistry.register(PlaceholderService.class, placeholderService);
        serviceRegistry.register(MaintenanceService.class, maintenanceService);
        serviceRegistry.register(DebugLogger.class, debugLogger);
        serviceRegistry.register(SidebarService.class, sidebarService);
        serviceRegistry.register(TablistService.class, tablistService);
        serviceRegistry.register(ClanRepository.class, clanRepository);
        serviceRegistry.register(ClanService.class, clanService);
        serviceRegistry.register(BanRepository.class, banRepository);
        serviceRegistry.register(BanService.class, banService);
        serviceRegistry.register(ReportRepository.class, reportRepository);
        serviceRegistry.register(ReportService.class, reportService);
        serviceRegistry.register(SpawnRepository.class, spawnRepository);
        serviceRegistry.register(SpawnService.class, spawnService);
        serviceRegistry.register(HomeRepository.class, homeRepository);
        serviceRegistry.register(HomeService.class, homeService);
        serviceRegistry.register(WarpRepository.class, warpRepository);
        serviceRegistry.register(WarpService.class, warpService);
        serviceRegistry.register(MuteRepository.class, muteRepository);
        serviceRegistry.register(MuteService.class, muteService);
        serviceRegistry.register(RegionRepository.class, regionRepository);
        serviceRegistry.register(RegionService.class, regionService);
        serviceRegistry.register(VanishService.class, vanishService);
        serviceRegistry.register(AuctionRepository.class, auctionRepository);
        serviceRegistry.register(AuctionService.class, auctionService);
        serviceRegistry.register(BloodMoonService.class, bloodMoonService);
        serviceRegistry.register(CoreAPI.class, api);
    }

    private void registerCommands() {
        CoreCommand coreCommand = new CoreCommand(this);

        commandManager.register("core", coreCommand, new CoreTabCompleter(this, coreCommand));
        commandManager.register("clan", new ClanCommand(this), new ClanTabCompleter());
        commandManager.register("rank", new RankCommand(this), new RankTabCompleter(this));

        commandManager.register("ban", new BanCommand(this));
        commandManager.register("unban", new UnbanCommand(this));
        commandManager.register("baninfo", new BanInfoCommand(this));

        commandManager.register("report", new ReportCommand(this));
        commandManager.register("reports", new ReportsCommand(this));

        commandManager.register("pay", new PayCommand(this));

        commandManager.register("spawn", new SpawnCommand(this));
        commandManager.register("setspawn", new SetSpawnCommand(this));

        commandManager.register("tpa", new TpaCommand(this));
        commandManager.register("tpaccept", new TpAcceptCommand(this));

        commandManager.register("rtp", new RtpCommand(this));

        commandManager.register("homes", new HomesCommand(this));
        commandManager.register("sethome", new SetHomeCommand(this));
        commandManager.register("home", new HomeCommand(this), new HomeTabCompleter(this));
        commandManager.register("delhome", new DelHomeCommand(this), new HomeTabCompleter(this));

        commandManager.register("setwarp", new SetWarpCommand(this), new WarpTabCompleter());
        commandManager.register("warp", new WarpCommand(this), new WarpTabCompleter());
        commandManager.register("warps", new WarpsCommand(this));

        commandManager.register("mute", new MuteCommand(this));
        commandManager.register("unmute", new UnmuteCommand(this));
        commandManager.register("clearchat", new ClearChatCommand(this));
        commandManager.register("slowchat", new SlowChatCommand(this));
        commandManager.register("chatmute", new ChatMuteCommand(this));

        commandManager.register("region", new RegionCommand(this));
        commandManager.register("coins", new CoinsCommand(this));

        commandManager.register("npc", new NpcCommand(this));

        commandManager.register("level", new LevelCommand(this));
        commandManager.register("gm", new GameModeCommand(this));
        commandManager.register("fly", new FlyCommand(this));
        commandManager.register("vanish", new VanishCommand(this));
        commandManager.register("auktion", new AuctionCommand(this));

        commandManager.register("nether", new NetherCommand(this));
        commandManager.register("end", new EndCommand(this));

        commandManager.register("image", new ImageCommand(this));
        commandManager.register("bloodmoon", new BloodMoonCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ReportGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatFormatListener(this), this);
        getServer().getPluginManager().registerEvents(new MotdListener(this), this);
        getServer().getPluginManager().registerEvents(new BanListener(this), this);
        getServer().getPluginManager().registerEvents(new ClanKillDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new GraveListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new FirstJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new WarpsGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new RegionListener(this), this);
        getServer().getPluginManager().registerEvents(new ClanClaimListener(this), this);
        getServer().getPluginManager().registerEvents(new NpcListener(this), this);
        getServer().getPluginManager().registerEvents(new LevelListener(this), this);
        getServer().getPluginManager().registerEvents(new VanishListener(this), this);
        getServer().getPluginManager().registerEvents(new AuctionListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnRespawnListener(this), this);
        getServer().getPluginManager().registerEvents(new BloodMoonListener(this), this);
    }

    public void reloadCore() {
        configManager.reload();
        messageManager.reload();
        prefix = messageManager.prefix();

        maintenanceService.load();
        databaseManager.createTables();
        spawnService.load();
        regionService.load();

        npcService.load();

        moduleManager.reloadModules();
        permissionService.applyAll();
    }

    public static Core getInstance() {
        return instance;
    }

    public String prefix() {
        return prefix;
    }

    public ServiceRegistry services() {
        return serviceRegistry;
    }

    public ConfigManager configs() {
        return configManager;
    }

    public MessageManager messages() {
        return messageManager;
    }

    public DatabaseManager database() {
        return databaseManager;
    }

    public PlayerDataService players() {
        return playerDataService;
    }

    public PermissionService permissions() {
        return permissionService;
    }

    public SchedulerService scheduler() {
        return schedulerService;
    }

    public AutoSaveService autosave() {
        return autoSaveService;
    }

    public ModuleManager modules() {
        return moduleManager;
    }

    public CooldownService cooldowns() {
        return cooldownService;
    }

    public EconomyService economy() {
        return economyService;
    }

    public RankService ranks() {
        return rankService;
    }

    public PlaceholderService placeholders() {
        return placeholderService;
    }

    public MaintenanceService maintenance() {
        return maintenanceService;
    }

    public DebugLogger debug() {
        return debugLogger;
    }

    public PlayerDataRepository playerDataRepository() {
        return playerDataRepository;
    }

    public SidebarService sidebar() {
        return sidebarService;
    }

    public TablistService tablist() {
        return tablistService;
    }

    public ClanService clans() {
        return clanService;
    }

    public ClanRepository clanRepository() {
        return clanRepository;
    }

    public BanService bans() {
        return banService;
    }

    public ReportService reports() {
        return reportService;
    }

    public SpawnService spawns() {
        return spawnService;
    }

    public HomeService homes() {
        return homeService;
    }

    public TpaService tpa() {
        return tpaService;
    }

    public CombatService combat() {
        return combatService;
    }

    public WarpService warps() {
        return warpService;
    }

    public MuteService mutes() {
        return muteService;
    }

    public RegionService regions() {
        return regionService;
    }

    public ClanClaimService claims() {
        return clanClaimService;
    }
    public ClanClaimVisualizer claimVisualizer() {
        return clanClaimVisualizer;
    }

    public NpcService npcs() {
        return npcService;
    }

    public LevelService levels() {
        return levelService;
    }

    public VanishService vanish() {
        return vanishService;
    }

    public AuctionService auctions() {
        return auctionService;
    }

    public ImageService images() {
        return imageService;
    }

    public BloodMoonService bloodMoon() {
        return bloodMoonService;
    }

    public CoreAPI api() {
        return api;
    }
}
