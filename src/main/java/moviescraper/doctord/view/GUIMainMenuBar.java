package moviescraper.doctord.view;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import moviescraper.doctord.controller.BrowseDirectoryAction;
import moviescraper.doctord.controller.BrowseUriAction;
import moviescraper.doctord.controller.ChooseExternalMediaPlayerAction;
import moviescraper.doctord.controller.ChooseFavoriteGenresAction;
import moviescraper.doctord.controller.ChooseFavoriteTagsAction;
import moviescraper.doctord.controller.FileNameCleanupAction;
import moviescraper.doctord.controller.MoveToNewFolderAction;
import moviescraper.doctord.controller.OpenFileAction;
import moviescraper.doctord.controller.PlayMovieAction;
import moviescraper.doctord.controller.RefreshDirectoryAction;
import moviescraper.doctord.controller.SelectAmalgamationSettingsAction;
import moviescraper.doctord.controller.WriteFileDataAction;
import moviescraper.doctord.controller.amalgamation.ScrapeAmalgamatedAction;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfileItem;
import moviescraper.doctord.controller.siteparsingprofile.SpecificProfileFactory;
import moviescraper.doctord.controller.siteparsingprofile.SiteParsingProfile.ScraperGroupName;
import moviescraper.doctord.model.Movie;
import moviescraper.doctord.model.preferences.MoviescraperPreferences;

public class GUIMainMenuBar extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MoviescraperPreferences preferences;
	private GUIMain guiMain;

	private JMenuItem writeFileMenuItem;

	public GUIMainMenuBar(GUIMain guiMain) {
		this.preferences = guiMain.getPreferences();
		this.guiMain = guiMain;
		initializeMenus();
	}

	/**
	 * Allows you to create a new JCheckBoxMenuItem using Lambda expressions. The preferenceSetterFunction function will be called to change the value when the
	 * menu item is checked and the initial value will be determined by the value returned by preferenceGetterFunction.
	 * 
	 * @param checkboxTitle - Text of menu item to create
	 * @param preferenceSetterFunction - setter function called when checkbox item clicked
	 * @param preferenceGetterFunction - function to return initial value of the checkbox
	 * @return
	 */
	private JCheckBoxMenuItem createCheckBoxMenuItem(String checkboxTitle, Consumer<Boolean> preferenceSetterFunction, Supplier<Boolean> preferenceGetterFunction) {
		JCheckBoxMenuItem checkBoxMenuItemToCreate = new JCheckBoxMenuItem(checkboxTitle);
		checkBoxMenuItemToCreate.setState(preferenceGetterFunction.get());
		checkBoxMenuItemToCreate.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				preferenceSetterFunction.accept(true);
			} else if (e.getStateChange() == ItemEvent.DESELECTED) {
				preferenceSetterFunction.accept(false);
			}

		});
		return checkBoxMenuItemToCreate;
	}

	private void initializePreferencesMenu() {

		//Set up the preferences menu
		JMenu preferenceMenu = new JMenu("偏好");
		preferenceMenu.setMnemonic(KeyEvent.VK_P);
		preferenceMenu.getAccessibleContext().setAccessibleDescription("偏好设置");
		createAllCheckBoxMenusForPreferencesMenu(preferenceMenu);
		add(preferenceMenu);

	}

	private void createAllCheckBoxMenusForPreferencesMenu(JMenu preferenceMenu) {

		if (preferenceMenu != null) {
			preferenceMenu.add(createFileCreationMenu());
			preferenceMenu.add(createTranslationMenu());
			preferenceMenu.add(createScrapingSearchOptionsMenu());
			preferenceMenu.add(createScrapingDialogsToShowMenu());
			preferenceMenu.add(createModifiyScrapedResultsByMenu());
			preferenceMenu.add(createRenamingMenu());
			preferenceMenu.add(createCleanUpFileNameMenu());
		}
	}

	private JMenu createCleanUpFileNameMenu() {
		JMenu submenu = new JMenu("清理文件名");

		//Checkbox for whether the user needs to manually confirm the results of each clean up file operation	
		JCheckBoxMenuItem confirmNameForFileNameCleanup = createCheckBoxMenuItem("每次清理时都需要手工确认",
		        b -> getPreferences().setConfirmCleanUpFileNameNameBeforeRenaming(b), () -> getPreferences().getConfirmCleanUpFileNameNameBeforeRenaming());
		submenu.add(confirmNameForFileNameCleanup);

		return submenu;
	}

	private JMenu createRenamingMenu() {
		JMenu submenu = new JMenu("重命名");

		//Checkbox for renaming Movie file
		JCheckBoxMenuItem renameMovieFile = createCheckBoxMenuItem("保存文件时自动重命名", b -> getPreferences().setRenameMovieFile(b), () -> getPreferences().getRenameMovieFile());
		submenu.add(renameMovieFile);

		return submenu;
	}

	private JMenu createModifiyScrapedResultsByMenu() {
		JMenu submenu = new JMenu("修改刮削结果");

		//Checkbox for option to append the ID to start of the title field
		JCheckBoxMenuItem appendIDToStartOfTitle = createCheckBoxMenuItem("标题头部增加番号", b -> getPreferences().setAppendIDToStartOfTitle(b),
		        () -> getPreferences().getAppendIDToStartOfTitle());
		submenu.add(appendIDToStartOfTitle);

		//Checkbox for option to use file name as the scraped title every time
		JCheckBoxMenuItem useFilenameAsScrapedMovieTitle = createCheckBoxMenuItem("标题字段总是使用文件名", b -> getPreferences().setUseFileNameAsTitle(b),
		        () -> getPreferences().getUseFileNameAsTitle());
		submenu.add(useFilenameAsScrapedMovieTitle);

		return submenu;
	}

	private JMenu createScrapingDialogsToShowMenu() {
		JMenu submenu = new JMenu("Scraping Dialogs to Show");

		//Checkbox for scraping dialog box allowing the user to override the URL used when scraping
		JCheckBoxMenuItem promptForUserProvidedURL = createCheckBoxMenuItem("Provide URL Manually", b -> getPreferences().setPromptForUserProvidedURLWhenScraping(b),
		        () -> getPreferences().getPromptForUserProvidedURLWhenScraping());
		submenu.add(promptForUserProvidedURL);

		//Checkbox for scraping dialog box allowing the user to override the URL used when scraping
		JCheckBoxMenuItem considerUserSelectionOneURL = createCheckBoxMenuItem("Consider All Selections one Item", b -> getPreferences().setConsiderUserSelectionOneURLWhenScraping(b),
		        () -> getPreferences().getConsiderUserSelectionOneURLWhenScraping());
		submenu.add(considerUserSelectionOneURL);

		//Checkbox for whether the user needs to manually select the art while scraping
		JCheckBoxMenuItem selectArtManuallyWhenScraping = createCheckBoxMenuItem("Select Art Manually", b -> getPreferences().setSelectArtManuallyWhenScraping(b),
		        () -> getPreferences().getSelectArtManuallyWhenScraping());
		submenu.add(selectArtManuallyWhenScraping);

		//Checkbox for whether the user needs to manually select the search result when scraping	
		JCheckBoxMenuItem selectSearchResultManuallyWhenScraping = createCheckBoxMenuItem("Select Search Results Manually", b -> getPreferences().setSelectSearchResultManuallyWhenScraping(b),
		        () -> getPreferences().getSelectSearchResultManuallyWhenScraping());
		submenu.add(selectSearchResultManuallyWhenScraping);

		return submenu;
	}

	private JMenu createScrapingSearchOptionsMenu() {
		JMenu submenu = new JMenu("刮削选项");

		//Checkbox for option if the ID is just considered the first word in the file
		JCheckBoxMenuItem isFirstWordOfFileID = createCheckBoxMenuItem("番号再文件名头部(默认尾部)", b -> getPreferences().setIsFirstWordOfFileID(b),
		        () -> getPreferences().getIsFirstWordOfFileID());
		submenu.add(isFirstWordOfFileID);

		return submenu;
	}

	private JMenu createTranslationMenu() {
		JMenu submenu = new JMenu("翻译");

		//Checkbox for scraping JAV files in japanese instead of english when clicking scrape jav
		JCheckBoxMenuItem scrapeInJapanese = createCheckBoxMenuItem("刮削日文信息(默认英语)", b -> getPreferences().setScrapeInJapanese(b),
		        () -> getPreferences().getScrapeInJapanese());
		submenu.add(scrapeInJapanese);

		return submenu;
	}

	private JMenu createFileCreationMenu() {
		JMenu submenu = new JMenu("文件创建");

		//Checkbox for writing fanart and poster
		JCheckBoxMenuItem writeFanartAndPosters = createCheckBoxMenuItem("保存封面和海报图片", b -> getPreferences().setWriteFanartAndPostersPreference(b),
		        () -> getPreferences().getWriteFanartAndPostersPreference());
		submenu.add(writeFanartAndPosters);

		//Checkbox for overwriting writing actors to .actor folder	
		JCheckBoxMenuItem writeActorImages = createCheckBoxMenuItem("保存演员图片", b -> getPreferences().setDownloadActorImagesToActorFolderPreference(b),
		        () -> getPreferences().getDownloadActorImagesToActorFolderPreference());
		submenu.add(writeActorImages);

		//Checkbox for scraping extrafanart		
		JCheckBoxMenuItem scrapeExtraFanart = createCheckBoxMenuItem("Write Extrafanart When Writing Data to a Directory or Moving File to a Directory",
		        b -> getPreferences().setExtraFanartScrapingEnabledPreference(b), () -> getPreferences().getExtraFanartScrapingEnabledPreference());
		submenu.add(scrapeExtraFanart);

		//Checkbox for also creating folder.jpg	in addition to the poster file jpg	
		JCheckBoxMenuItem createFolderJpg = createCheckBoxMenuItem("Create folder.jpg for Each Folder", b -> getPreferences().setCreateFolderJpgEnabledPreference(b),
		        () -> getPreferences().getCreateFolderJpgEnabledPreference());
		submenu.add(createFolderJpg);

		//Checkbox for writing the trailer to file
		JCheckBoxMenuItem writeTrailerToFile = createCheckBoxMenuItem("下载预告片并将其写入文件", b -> getPreferences().setWriteTrailerToFile(b),
		        () -> getPreferences().getWriteTrailerToFile());
		submenu.add(writeTrailerToFile);

		//Checkbox for overwriting fanart and poster
		JCheckBoxMenuItem overwriteFanartAndPosters = createCheckBoxMenuItem("覆盖 Fanart, Poster folder.jpg 文件", b -> getPreferences().setOverWriteFanartAndPostersPreference(b),
		        () -> getPreferences().getOverWriteFanartAndPostersPreference());
		submenu.add(overwriteFanartAndPosters);

		//Checkbox for using fanart.jpg and poster.jpg, not moviename-fanart.jpg and moviename-poster.jpg
		JCheckBoxMenuItem noMovieNameInImageFiles = createCheckBoxMenuItem("封面和还报文件保存为 fanart.jpg 和 poster.jpg(默认为 [movie]-fanart.jpg 和 moviename-poster.jpg)",
		        b -> getPreferences().setNoMovieNameInImageFiles(b), () -> getPreferences().getNoMovieNameInImageFiles());
		submenu.add(noMovieNameInImageFiles);

		//Checkbox for naming .nfo file movie.nfo instead of using movie name in file
		JCheckBoxMenuItem nfoNamedMovieDotNfo = createCheckBoxMenuItem(".nfo 文件保存为 movie.nfo (默认是影片番号)", b -> getPreferences().setNfoNamedMovieDotNfo(b),
		        () -> getPreferences().getNfoNamedMovieDotNfo());
		submenu.add(nfoNamedMovieDotNfo);

		//Checkbox for whether to write the <thumb> tags into the nfo file
		JCheckBoxMenuItem writeThumbTagsForPosterAndFanartToNfo = createCheckBoxMenuItem("图片信息保存到 .nfo 文件",
		        b -> getPreferences().setWriteThumbTagsForPosterAndFanartToNfo(b), () -> getPreferences().getWriteThumbTagsForPosterAndFanartToNfo());
		submenu.add(writeThumbTagsForPosterAndFanartToNfo);

		return submenu;
	}

	private void initializeSettingsMenu() {

		JMenu settingsMenu = new JMenu("设置");
		settingsMenu.setMnemonic(KeyEvent.VK_S);

		// This is a scraping preference but fits better under the Settings menu
		JMenuItem scrapersMenuItem = new JMenuItem("刮削器组合设置...");
		scrapersMenuItem.addActionListener(new SelectAmalgamationSettingsAction(guiMain));
		settingsMenu.add(scrapersMenuItem);

		JMenuItem renameSettings = new JMenuItem("重命名设置...");
		renameSettings.addActionListener(new ActionListener() {
			@SuppressWarnings("unused") //simply calling the new for RenamerGUI below will show the dialog
			@Override
			public void actionPerformed(ActionEvent e) {
				Movie currentSelectedMovie = null;
				File currentlySelectedFile = null;
				if (guiMain.getMovieToWriteToDiskList().size() > 0)
					currentSelectedMovie = guiMain.getMovieToWriteToDiskList().get(0);
				if (guiMain.getCurrentlySelectedMovieFileList().size() > 0)
					currentlySelectedFile = guiMain.getCurrentlySelectedMovieFileList().get(0);
				new RenamerGUI(getPreferences(), currentSelectedMovie, currentlySelectedFile);
			}
		});
		settingsMenu.add(renameSettings);

		JMenuItem favoriteGenresMenuItem = new JMenuItem("最爱的类型...");
		favoriteGenresMenuItem.addActionListener(new ChooseFavoriteGenresAction(guiMain));
		settingsMenu.add(favoriteGenresMenuItem);

		JMenuItem favoriteTagsMenuItem = new JMenuItem("最爱的标签...");
		favoriteTagsMenuItem.addActionListener(new ChooseFavoriteTagsAction(guiMain));
		settingsMenu.add(favoriteTagsMenuItem);

		JMenuItem externalMediaPlayerPickerMenu = new JMenuItem("选择外部播放器...");
		externalMediaPlayerPickerMenu.addActionListener(new ChooseExternalMediaPlayerAction());
		settingsMenu.add(externalMediaPlayerPickerMenu);

		add(settingsMenu);
	}

	private void initializeFileMenu() {
		// File menu

		JMenu fileMenu = new JMenu("文件");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("File actions for JAVMovieScraper");

		//Browse directory file menu
		JMenuItem browseDirectory = new JMenuItem("浏览文件夹...");
		browseDirectory.setMnemonic(KeyEvent.VK_B);
		browseDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK));
		browseDirectory.addActionListener(new BrowseDirectoryAction(guiMain));
		fileMenu.add(browseDirectory);

		//Refresh file menu
		JMenuItem refreshDirectory = new JMenuItem("刷新");
		refreshDirectory.setMnemonic(KeyEvent.VK_R);
		refreshDirectory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
		refreshDirectory.addActionListener(new RefreshDirectoryAction(guiMain));
		fileMenu.add(refreshDirectory);

		fileMenu.addSeparator();

		//Open file menu
		JMenuItem openFile = new JMenuItem("打开文件");
		openFile.setMnemonic(KeyEvent.VK_O);
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK));
		openFile.addActionListener(new OpenFileAction(guiMain));
		fileMenu.add(openFile);

		JMenuItem playMovie = new JMenuItem("播放影片");
		playMovie.setMnemonic(KeyEvent.VK_P);
		playMovie.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
		playMovie.addActionListener(new PlayMovieAction(guiMain));
		fileMenu.add(playMovie);

		writeFileMenuItem = new JMenuItem("保存信息文件");
		writeFileMenuItem.setToolTipText("Write out the .nfo file to disk. The movie must have a title for this to be enabled.");
		writeFileMenuItem.setEnabled(false); //this becomes enabled later when there is an actual movie to write
		writeFileMenuItem.setMnemonic(KeyEvent.VK_W);
		writeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
		writeFileMenuItem.addActionListener(new WriteFileDataAction(guiMain));
		fileMenu.add(writeFileMenuItem);

		JMenuItem moveFile = new JMenuItem("移动到新文件夹");
		moveFile.setMnemonic(KeyEvent.VK_M);
		moveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK));
		moveFile.addActionListener(new MoveToNewFolderAction(guiMain));
		fileMenu.add(moveFile);

		JMenuItem cleanFile = new JMenuItem("清理文件名");
		cleanFile.setMnemonic(KeyEvent.VK_C);
		cleanFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
		cleanFile.addActionListener(new FileNameCleanupAction(guiMain));
		fileMenu.add(cleanFile);

		fileMenu.addSeparator();

		//Exit file menu
		JMenuItem exit = new JMenuItem("退出");
		exit.setMnemonic(KeyEvent.VK_E);
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(exit);

		add(fileMenu);
	}

	private void initializeViewMenu() {

		JMenu viewMenu = new JMenu("视图");
		viewMenu.setMnemonic(KeyEvent.VK_V);

		JMenuItem consoleInSeperateWindowMenuItem = new JMenuItem("视图输出到新窗口");
		consoleInSeperateWindowMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MessageConsoleGUI.showWindow();
			}
		});

		JCheckBoxMenuItem consolePanelMenuItem = new JCheckBoxMenuItem("显示调试输出面板");
		consolePanelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		consolePanelMenuItem.setState(guiMain.getGuiSettings().getShowOutputPanel());
		consolePanelMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					guiMain.showMessageConsolePanel();
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					guiMain.hideMessageConsolePanel();
			}
		});

		JCheckBoxMenuItem buttonPanelMenuItem = new JCheckBoxMenuItem("显示工具栏");
		buttonPanelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		buttonPanelMenuItem.setState(guiMain.getGuiSettings().getShowToolbar());
		buttonPanelMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					guiMain.showButtonPanel();
				else if (e.getStateChange() == ItemEvent.DESELECTED)
					guiMain.hideButtonPanel();

			}
		});

		viewMenu.add(buttonPanelMenuItem);
		viewMenu.add(consolePanelMenuItem);
		viewMenu.add(consoleInSeperateWindowMenuItem);

		add(viewMenu);
	}

	private void initializeScrapeMenu() {
		JMenu scrapeMenu = new JMenu("刮削器");
		scrapeMenu.setMnemonic(KeyEvent.VK_S);

		JMenuItem scrapeAdultDVDAmalgamated = new JMenuItem(
		        new ScrapeAmalgamatedAction(guiMain, guiMain.getAllAmalgamationOrderingPreferences().getScraperGroupAmalgamationPreference(ScraperGroupName.AMERICAN_ADULT_DVD_SCRAPER_GROUP)));
		scrapeAdultDVDAmalgamated.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
		scrapeAdultDVDAmalgamated.setIcon(GUIMainButtonPanel.initializeImageIcon("App"));

		JMenuItem scrapeJAVAmalgamated = new JMenuItem(
		        new ScrapeAmalgamatedAction(guiMain, guiMain.getAllAmalgamationOrderingPreferences().getScraperGroupAmalgamationPreference(ScraperGroupName.JAV_CENSORED_SCRAPER_GROUP)));
		scrapeJAVAmalgamated.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
		scrapeJAVAmalgamated.setIcon(GUIMainButtonPanel.initializeImageIcon("Japan"));

		scrapeMenu.add(scrapeAdultDVDAmalgamated);
		scrapeMenu.add(scrapeJAVAmalgamated);

		JMenu specificMenu = new JMenu("特殊刮削器");
		scrapeMenu.add(specificMenu);

		int i = 0;

		for (SiteParsingProfileItem item : SpecificProfileFactory.getAll()) {
			JMenuItem menuItem = new JMenuItem(item.toString());
			Icon menuItemIcon = item.getParser().getProfileIcon();
			if (menuItemIcon != null)
				menuItem.setIcon(menuItemIcon);

			if (++i < 10) {
				menuItem.setAccelerator(KeyStroke.getKeyStroke(Character.forDigit(i, 10), Event.CTRL_MASK));
			} else if (i < 20) {
				if (i == 10)
					++i;
				menuItem.setAccelerator(KeyStroke.getKeyStroke(Character.forDigit(i % 10, 10), Event.CTRL_MASK | Event.SHIFT_MASK));
			}
			menuItem.addActionListener(new ScrapeAmalgamatedAction(guiMain, item.getParser()));
			specificMenu.add(menuItem);
		}

		add(scrapeMenu);
	}

	private void initializeHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		JMenuItem website = new JMenuItem("Visit website");
		website.addActionListener(new BrowseUriAction(BrowseUriAction.MainWebsiteUri));

		JMenuItem reportBug = new JMenuItem("Report bug");
		reportBug.addActionListener(new BrowseUriAction(BrowseUriAction.ReportBugUri));

		JMenuItem about = new JMenuItem("About...");
		about.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new AboutDialog(guiMain.getFrmMoviescraper()).setVisible(true);
			}
		});

		helpMenu.add(website);
		helpMenu.add(reportBug);
		helpMenu.addSeparator();
		helpMenu.add(about);

		add(helpMenu);
	}

	private void initializeMenus() {
		//add the various menus together
		initializeFileMenu();
		initializeScrapeMenu();
		initializePreferencesMenu();
		initializeSettingsMenu();
		initializeViewMenu();
		initializeHelpMenu();
	}

	private MoviescraperPreferences getPreferences() {
		return preferences;
	}

	public void disableWriteFile() {
		if (writeFileMenuItem != null) {
			writeFileMenuItem.setEnabled(false);
		}
	}

	public void enableWriteFile() {
		if (writeFileMenuItem != null) {
			writeFileMenuItem.setEnabled(true);
		}
	}

}
