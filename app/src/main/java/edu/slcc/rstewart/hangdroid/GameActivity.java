package edu.slcc.rstewart.hangdroid;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
  private String Word;
  private int wordLen = 0;
  private int badGuessCount= 0;
  private int okGuessCount = 0;
  private String badGuesses = "";
  private String correctGuesses = "";
  private String[] wordArray;
  private int wordCount = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    // populate the wordArray
    populateWordArray();

    // load a textView for each letter
    loadTextViews();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.hangdroid_menu,menu);
    return true;
  }


  public void makeGuess(View v){
    // reference the input box, get text, and clear input
    EditText et1 = (EditText)findViewById(R.id.et1);
    String s = et1.getText().toString().toUpperCase();
    et1.setText("");

    // initialize tracking variables
    boolean isValidTF = false;
    boolean guessCorrect = false;
    String t = "";
    int k = 0;

    // validate text
    isValidTF = validateGuess(s);
    if(!isValidTF){return;}

    // scan Word for the guessed character
    for(int i = 0; i < wordLen; i++){

      // get the letter at position i
      t = Word.substring(i, i + 1);

      // if the guess matches the letter from Word, show it,
      // set the flag that the letter was found, and increment okGuessCount
      k = t.compareTo(s);
      if(k == 0){
        guessCorrect = true;
        correctGuesses += s;
        okGuessCount++;
        LinearLayout LL = (LinearLayout)findViewById(R.id.gameact);
        TextView TV = (TextView)LL.getChildAt(i);
        TV.setText(t);
      }
    }

    // if all letters are filled in, end this game
    // otherwise, if guess was correct just exit
    if(okGuessCount >= wordLen){
      callGameOver(3, true);
      return;}
    else if(guessCorrect){return;}

    // if you're still in this sub, then the letter wasn't found
    // show the approprite pic
    documentWrongGuess(s);
  }


  private void documentWrongGuess(String s){
    // show the next image in the sequence
    showNextImage();

    // add the incorrect letter to the wrong guess list
    addIncorrectLetter(s);

    // if the number of bad guesses is 6 or more, end game
    if(badGuessCount >= 6){callGameOver(3, false);}
  }


  private void showNextImage(){
    // increment the game index and construct expected filename
    badGuessCount++;
    String fn = "hangdroid_" + badGuessCount;
    String packNa = getPackageName();

    // get application resources and the resource ID for the next pic
    Resources res = getResources();
    int resID = res.getIdentifier(fn,"drawable",packNa);

    // reference the ImageView and set its image
    ImageView IV = (ImageView)findViewById(R.id.iv2);
    IV.setImageResource(resID);
  }


  private void addIncorrectLetter(String s){
    int L = badGuesses.length();
    if(L == 0){badGuesses = s;}
    else {badGuesses += (", " + s);}
    TextView TV = (TextView)findViewById(R.id.tv9);
    TV.setText(badGuesses);
  }


  private boolean validateGuess(String s){
    // check if game is over
    if(badGuessCount>= 6){
      HDUtils.alert(this, "Game is over.");
      return false;
    }

    // make sure string length is just one character
    long L = s.length();
    if(L != 1){
      HDUtils.alert(this, "Enter a single letter only.");
      return false;
    }

    // make sure string has alphabetic characters only
    char ch = s.charAt(0);
    if(ch < 65 || ch > 90){
      HDUtils.alert(this, "Enter alphabetical characters only.");
      return false;
    }

    // make sure letter isn't already in incorrect list
    int i = badGuesses.indexOf(s);
    if(i != -1){
      HDUtils.alert(this, "The letter " + s + " has already been guessed.");
      return false;
    }

    // make sure letter isn't already in correct list
    i = correctGuesses.indexOf(s);
    if(i != -1){
      HDUtils.alert(this, "The letter " + s + " is already shown.");
      return false;
    }

    // return true since all checks passed
    return true;
  }  // end fcn validateGuess


  private String getWord(){
    // get a random integer between 0 and wordCount -1
    double rnd = Math.random();
    int k = (int)(rnd * wordCount);
    boolean tf = false;
    String s = "";
    String t = "";

    // get a random word from array, record its length, and return it
    // check that the word has 4-10 chars
    do {
      t = wordArray[k];
      tf = HDUtils.validateWord(t, false, this);
    } while (!tf);

    // set word length and return the word
    wordLen = t.length();
    return t;
  }


  private void loadTextViews(){
    // get a word to guess from the Intent or from the hard-coded array
    Intent I = getIntent();
    String s = I.getStringExtra("secretWord");
    if(s == null){s = getWord();}
    else if(s == ""){s = getWord();}

    // clear the secret word from the Intent so it is not
    // reused when back button is pressed
    I.putExtra("secretWord", "");

    // capitalize, trim, and validate the word
    Word = HDUtils.cleanWord(s);
    wordLen = Word.length();

    // reference the linear layout that will contain
    // the TextViews
    LinearLayout LL = (LinearLayout)findViewById(R.id.gameact);
    TextView TV = null;
    int k = wordLen;

    // remove any previous children
    while(LL.getChildCount() > 0){LL.removeView(LL.getChildAt(0));}

    // convert margin of 5 device-independent-pixels to
    // absolute pixels for the running device
    int px = convertDpToPx(5);

    // get an integer for a black color
    int blk = Color.parseColor("#ff000000");

    // add a styled TextView for each letter
    // max number of letters is 10
    if(k > 10){k = 10;}
    for(int i = 0; i < k; i++){

      // create a new TextView
      TV = new TextView(this);

      // get the character in position i
      String t = s.substring(i, i + 1);

      // enter a space or an underscore
      // since spaces cannot be guessed, give credit for them here
      if(t.compareTo(" ") == 0){
        TV.setText(" ");
        okGuessCount++;}
      else {TV.setText("_");}

      // style the TextView
      TV.setCursorVisible(false);
      TV.setAllCaps(true);
      TV.setTextSize(40);
      TV.setTextColor(blk);
      TV.setPadding(px, px, px, px);

      // append the TextView to its LinearLayout
      LL.addView(TV);
    }
  }


  private int convertDpToPx(int dp){
    // create variables for Resources and DisplayMetrics
    Resources rsrc = this.getResources();
    DisplayMetrics DM = rsrc.getDisplayMetrics();

    // use TypedValue to convert to device-independent-pixels
    // to the absolute pixes (px) needed for LayoutParams object
    int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, DM);
    return px;
  }


  private void clearGamePage(){
    // clear incorrect letters list
    badGuesses = "";
    badGuessCount = 0;
    TextView TV = (TextView)findViewById(R.id.tv9);
    TV.setText("");

    // reset image to default
    ImageView IV = (ImageView)findViewById(R.id.iv2);
    IV.setImageResource(R.drawable.hangdroid_0);

    // reset game counters
    correctGuesses = "";
    okGuessCount = 0;

    // get a new word and set it
    loadTextViews();
  }  // end fcn clearGamePage


  private void populateWordArray(){
    // string of variable-length words
    String s = "able achieve acoustics action activity aftermath afternoon apparel appliance beginner believe bomb border boundary breakfast cabbage cable calculator calendar caption carpenter cemetery channel circle creator creature education faucet feather friction fruit fuel galley guide guitar health heart idea kitten laborer language lawyer linen locket lumber magic minister mitten money mountain music partner passenger pickle picture plantation plastic pleasure pocket police pollution railway recess reward route scene scent squirrel stranger suit sweater temper territory texture thread treatment veil vein volcano wealth weather wilderness wren wrist writer";
    s += "abase abbey abbot abdicate abdomen abdominal abduction abed abet abeyance abhorrent abidance abject abjure ablution abnegate abnormal abominate abrade abrasion abridge abrogate abrupt abscess abscond absence absolve absorb abstain abstruse absurd abundant abusive abut abyss academic academy accede accept access accession accessory acclaim accompany accordion accost account accouter accredit accuracy accurate accursed accuse accustom acerbity acetate acetic ache Achillean acid acidify acme acoustic acquaint acquiesce acquire acquit acquittal acreage acrid acrimony actuality actuary actuate acumen acute adamant addendum addle adduce adhere adherence adherent";
    s += "adhesion adieu adjacency adjacent adjudge adjunct adjutant admonish ado adoration adroit adumbrate advent adverse adversity advert advisory advocacy advocate aerial aeronaut aerostat affable affect affiliate affix affluence affront afire afoot aforesaid afresh aggravate aggregate aggress aggrieve aghast agile agitate agrarian ailment airy akin alabaster alacrity albeit albino album alchemy alcohol alcove alder alderman alias alien alienable alienate aliment alkali allay allege allegory alleviate alley alliance allot allotment allude allusion alluvion ally almanac aloof altar alter altercate alternate altitude alto altruism altruist amalgam amateur amatory";
    s += "ambiguous ambitious ambrosial ambulance ambulate ambush amenable amicable amity amorous amorphous amour ampere ampersand amplitude amply amputate amusement anagram analogous analogy analyst analyze anarchy anathema anatomy ancestry anecdote anemia anemic anew angelic angular anhydrous animate animosity annalist annals annex annotate annual annuity anode anonymous Antarctic ante antecede antedate antenatal anterior anteroom anthology antic antidote antilogy antiphon antiphony antipodes antiquary antiquate antique antitoxin antonym anxious apathy aperture apex aphorism apiary apogee apology apostasy apostate apostle appall apparent appease appellate append appertain";
    s += "apposite appraise apprehend aqueduct aqueous arbiter arbitrary arbitrate arbor arboreal arboretum arcade archaic archaism archangel archetype ardent ardor arid armada armful armory aroma arraign arrange arrant arrear arrival arrogant arrogate Artesian artful Arthurian artifice artless ascendant ascension ascent ascetic ascribe asexual ashen askance asperity aspirant aspire assailant assassin assay assent assess assessor assets assiduous assignee assonance assonant assonate assuage astute atheism athirst athwart atomizer atone atonement atrocious atrocity attache attest auburn audacious audible audition auditory augment augur aura aural auricle auricular aurora auspice austere autarchy authentic autocracy autocrat automaton autonomy autopsy autumnal auxiliary avalanche avarice aver averse aversion avert aviary avidity avocation avow awaken awry aye azalea azure Baconian bacterium badger baffle bailiff baize bale baleful ballad balsam banal barcarole barograph barometer barring baritone bask bass baste baton battalion batten batter bauble bawl beatify beatitude beau becalm beck bedaub bedeck bedlam befog befriend beget begrudge belate belay belie believe belittle belle bellicose bemoan benefice benefit benign benignant benignity benison bequeath bereave berth beseech beset besmear bestial bestrew bestride bethink betide betimes betroth betrothal bevel bewilder bibulous bide biennial bier bigamist bigamy bight bilateral bilingual biograph biography biology biped blase blaspheme blatant blaze blazon bleak blemish blithe blockade boatswain bodice bodily bole bolero boll bolster bomb bombard bombast boorish bore borough bosom botanical botanize botany bountiful bowler boycott brae braggart brandish bravado bravo bray braze brazier breach breaker breech brethren brevity bridle brigade brigadier brigand brimstone brine bristle Britannia Briticism brittle broach broadcast brogan brogue brokerage bromine bronchus brooch browbeat brusque buffoon bulbous bullock bulrush bulwark bumper bumptious bungle buoyancy buoyant bureau burgess burgher burnish bursar bustle butt butte buttress cabal cabalism cabinet cacophony cadence cadenza caitiff cajole cajolery calculus callosity callow calorie calumny Calvary Calvinism Calvinize came cameo campaign Canaanite canary candid candor canine canon cant cantata canto capacious capillary caprice caption captious captivate carcass cardiac cardinal caret carnage carnal carouse carrion cartilage cartridge caste castigate casual casualty cataclysm cataract cathode caucus causal caustic cauterize cede censor census centenary centurion cereal cessation cession chagrin chameleon chancery chaos charlatan chasm chasten chastise chastity chateau chattel check chiffon chivalry cholera choleric choral Christ christen chromatic cipher circulate citadel cite claimant clamorous clan clangor clarify clarion classify clearance clemency clement clothier clumsy coagulate coagulant coalition coddle codicil coerce coercion coercive cogent cognate cognizant cohere cohesion cohesive coincide collapse colleague collector collegian collide collier collision colloquy collusion colossus comely comical commingle committal commodity commotion commute competent complex compliant component comport composure compress comprise compute concede conceit conceive concerto concord concur condense conduce conducive conduit confer conferee confessor confidant confide confident confluent confront congeal congenial congest conjoin conjugal conjugate connive connote connubial conquer conscious conscript consensus consign consignee consignor console consonant consort conspire constable constrict consul consulate contagion contender contort contrite contrive control contumacy contuse contusion convene converge convex convivial convolve convoy convulse copious coquette cornice corollary coronet corporal corporate corporeal corps corpse corpulent corpuscle correlate corrode corrosion corrosive cosmetic cosmic cosmogony cosmology cosmos course courser courtesy covenant covert covey cower coxswain crag cranium crass craving creak creamery creamy credence credible credulous creed crematory crevasse crevice criterion critique crockery crucible crusade cudgel culinary cull culpable culprit culvert cupidity curable curator curio cursive cursory curt curtail curtsy cycloid cygnet cynical cynicism cynosure daring darkling Darwinism dastard datum dauntless dearth debase debatable debonair debut decagon decagram decaliter decalogue Decameron decameter decamp decapod deceit deceitful deceive decency decent deciduous decimal decimate decipher decisive decorate decorous decoy decrepit deduce deface defalcate defame default defendant defensive defer deference defiant deficient definite deflect deforest deform deformity defraud defray degrade dehydrate deify deign deist deity deject dejection delicacy delineate delirious delude deluge delusion demagogue demeanor demented demerit demise demolish demulcent demurrage dendroid denizen denote denounce denude deplete deplore deponent deport depositor deprave deprecate depress depth derelict deride derisible derision derive derrick descent descry desert desiccant designate desist despair desperado desperate despite despond despot despotism destitute desultory deter deterrent detest detract detriment detrude deviate devilry deviltry devious devise devout dexterity diabolic diagnose diagnosis dialect dialogue diatomic diatribe dictum didactic diffident diffusion dignitary digraph digress dilate dilatory dilemma diligence dilute dimly diphthong diplomacy diplomat disagree disallow disappear disarm disavow disavowal disburden disburse discard disciple disclaim discolor discomfit discord discover discredit discreet disengage disfavor disfigure dishonest disinfect dislocate dismissal dismount disown disparage disparity dispel displace disquiet disregard disrepute disrobe disrupt dissect dissemble dissent dissever dissipate dissolute dissolve dissonant dissuade distemper distend distill distiller distort distrain distrust disunion diurnal divergent diverse diversion diversity divert divest divinity divisible divisor divulge docile docket doe dogma dogmatic dogmatize doleful dolesome dolor dolorous domain domicile dominance dominant dominate domineer donate donator donee donor dormant doublet doubly dowry drachma dragnet dragoon drainage dramatist dramatize drastic drought drowsy drudgery dubious duckling ductile duet dun duplex duplicity durance duration duteous dutiable dutiful dwindle dyne earnest eatable ebullient eccentric eclipse economize ecstasy ecstatic edible edict edify editorial educe efface effect effective effectual effete efficacy efficient effluvium effuse effusion egoism egoist egotism egotist egregious egress eject elapse elegy element elicit eligible eliminate elocution eloquent elucidate elude elusion emaciate emanate embargo embark embarrass embellish embezzle emblazon emblem embody embolden embolism embroil emerge emergence emergent emeritus emigrant emigrate eminence eminent emit emphasis emphasize emphatic employee employer emporium empower emulate enact enamor encamp encomium encompass encore encourage encroach encumber endanger endear endemic endue endurable endurance energetic enervate enfeeble engender engrave engross enhance enigma enjoin enkindle enlighten enlist enmity ennoble enormity enormous enrage enrapture enshrine ensnare entail entangle enthrall enthrone enthuse entirety entrails entreaty entree entrench entwine enumerate epic epicure Epicurean epicycle epidemic epidermis epigram epilogue epiphany episode epitaph epithet epitome epizootic epoch epode equalize equitable equity equivocal eradicate errant erratic erroneous erudite erudition eschew espy esquire essence esthetic estimable estrange estuary et eugenic eulogize eulogy euphemism euphony eureka evade evanesce evasion eventual evert evict evince evoke evolution evolve excavate exceed excel excellent excerpt excess excitable exclude exclusion excretion excursion excusable execrable executor exegesis exemplar exemplary exemplify exempt exert exhale exhaust exhume exigency exigent existence exit exodus exonerate exorcise exotic expand expanse expansion expect expedient expedite expend expense expiate explicate explicit explode explosion explosive exposure expulsion extant extempore extension extensive extensor extenuate exterior external extinct extol extort extortion extradite extremist extremity extricate extrude exuberant fabricate fabulous facet facetious facial facile facility facsimile faction factious fallacy fallible fallow famish fanatic fancier fanciless fathom fatuous faulty faun fawn fealty feasible federate feint felicity felon felonious felony feminine fernery ferocious ferocity fervent fervid fervor festal festive fete fetus feudal feudalism fez fiasco fickle fidelity fiducial fief finale finality finally financial financier finery finesse finite fiscal fissure fitful fixture flagrant flection fledgling flexible flimsy flippant floe flora floral florid florist fluctuate flue fluent fluential flux foggy foible foist foliage folio fondle foolery foppery foppish forby forcible forecourt forejudge forepeak foreshore forebode forecast foreclose forego forehead foreign foreigner foreman forerun foresail foresee foresight foretell forfeit forfend forgery forgo formation formula forswear forte forth fortify fortitude foursome fracture fragile frailty fragile frantic fraternal fray freemason free frequency fresco freshness fretful frightful frigid frivolity frivolous frizz frizzle frontier frowzy frugal fruition fugacious fulcrum fulminate fulsome fumigate fungible fungous fungus furbish furlong furlough furrier further furtive fuse fusible futile futurist gauge gaiety gaily gait gallant galore galvanic galvanism galvanize gamble gambol gamester gamut garnish garrison garrote garrulous gaseous gastric gastritis gendarme genealogy generally generate generic genesis geniality genital genitive genteel gentile geology germane germinate gestation gesture ghastly gibe giddy gigantic giver glacial glacier gladden glazier glimmer glimpse globose globular glorious glutinous gnash Gordian gourmand gosling gossamer gourd graceless gradation gradient granary grandeur grandiose grantee grantor granular granulate granule grapple gratify gratuity gravity grenadier grief grievance grievous grimace grisly grotesque grotto ground guess guile guileless guinea guise gullible gumption gusto guy guzzle gyrate gyroscope habitable habitant habitual habitude hackney haggard halcyon hale harangue harass harbinger hardihood havoc hawthorn hazard head head heedless heifer heinous henchman henpeck heptagon heptarchy herbarium heredity heresy heretic heritage hernia hesitancy hesitant heterodox hexapod hexagon hiatus hibernal Hibernian hideous hilarious hillock hinder hindmost hindrance hirsute hoard hoarse homage homonym homophone hoodwink horde hosiery hostility huckster humane humanize humbug humiliate hussar hustle hybrid hydra hydraulic hydrous hygiene hypnosis hypnotic hypnotism hypnotize hypocrisy hypocrite hysteria ichthyic icily iciness icon idealize idiom idolize ignoble Iliad illegal illegible illiberal illicit illogical illumine illusion illusive illusory imaginary imbibe imbroglio imbrue imitation imitator immature immense immerse immersion immigrant immigrate imminence imminent immoral immovable immune immutable impair impartial impassive impede impel impend imperil imperious impetuous impetus impiety impious impliable implicate implicit imply impolitic importune impotent impromptu improper improvise imprudent impudence impugn impulsion impulsive impunity impure impute inactive inane inanimate inapt inaudible inborn inbred incentive inception inceptive incessant inchmeal inchoate incidence incident incipient incisor incite indelible indicant indicator indict indigence indigent indignant indignity indolence indolent induct indulgent inebriate inedible ineffable inept inert infamous infamy inference infernal infest infidel infinite infinity infirm infirmary infirmity influence influx infringe infuse infusion ingenious ingenuity ingenuous ingraft inherence inherent inhibit inhuman inhume inimical iniquity initiate inject inkling inland inlet inmost innocuous innovate innuendo inquire inroad inscribe insecure insidious insight insinuate insipid insistent insolence insolent insomnia inspector instance instant instigate instill insular insulate insurgent integrity intellect intension intensive intention interact intercede intercept interdict interim interlude intermit interpose interrupt intersect intervale intervene intestacy intestate intestine intimacy intrepid intricacy intricate intrigue intrinsic intromit introvert intrude intrusion intuition inundate inure invalid invalid invasion invective inveigh inventive inverse inversion invert investor invidious invoke involve inwardly iota irascible irate ire irk irksome irony irradiate irrigant irrigate irritable irritancy irritant irritate irruption isle islet isobar isolate itinerant itinerary itinerate jargon jaundice Jingo jocose jocular joggle jovial judgment judicial judiciary judicious juggle jugglery jugular juicy junction juncture junta juridical juror joust juvenile juxtapose keepsake kerchief kernel kiln kiloliter kilometer kilowatt kimono kingling kingship kinsfolk knavery knead knight laborious labyrinth lacerate lactation lacteal lactic laddie ladle laggard landlord landmark landscape languid languor lapse lassie latent latency later lateral latish lattice laud laudable laudation laudatory laundress laureate lave lawgiver lawmaker lax laxative lea leaflet leaven leeward legacy legalize legging legible legionary legislate leisure leniency lenient leonine lethargy levee lever leviathan levity levy lewd lexicon liable libel liberate licit liege lien lieu lifelike lifelong lifetime ligament ligature ligneous likely liking linear liner lingo lingua lingual linguist liniment liquefy liqueur liquidate liquor listless literacy literal lithe lithesome lithotype litigant litigate litigious littoral liturgy livid loam loath loathe locative loch lode lodgment logic logical logician loiterer longevity loot lordling lough louse lovable lowly lucid lucrative ludicrous luminary luminous lunacy lunar lunatic lune lurid luscious lustrous luxuriant luxuriate lying lyre lyric machinery machinist macrocosm madden Madonna magician magnate magnet magnetize magnitude maharaja maintain maize makeup malady malaria malign malignant malleable mallet maltreat mandate mandatory mane maneuver mania maniac manifesto manlike manliness mannerism manor mantel mantle manumit marine maritime maroon martial Martian martyrdom marvel masonry massacre massive mastery material maternal matinee matricide matrimony matrix matter maudlin mausoleum mawkish maxim maze mead meager meander mechanics medallion medial mediate medicine medieval mediocre medley meliorate melodious melodrama memento memorable menace menagerie mendicant mentality mentor mercenary merciful merciless mesmerize messieurs metal metaphor mete metonymy metric metronome mettle microcosm midsummer midwife mien migrant migrate migratory mileage militant militate militia Milky millet mimic miniature minimize minion ministry minority minute minutia mirage misbehave miscount miscreant misdeed miser mishap mislay mismanage misnomer misogamy misogyny misplace misrule missal missile missive mistrust misty misuse mite miter mitigate mnemonics moat mobocracy moccasin mockery moderator modernity modernize modify modish modulate mollify molt momentary momentous momentum monarchy monastery monetary mongrel monition monitory monocracy monogamy monogram monograph monolith monologue monomania monopoly monotone monotony monsieur moonbeam morale moralist morality moralize morbid mordant moribund morose motley motto mouthful muddle muffle mulatto muleteer multiform mundane municipal muster mutation mutilate mutiny myriad mystic myth mythology nameless naphtha Narcissus narrate narration narrative narrator nasal natal naturally nausea nauseate nauseous nautical naval navel navigable navigate nebula necessary necessity necrology necrosis nectar nectarine needy nefarious negate negation negligee negligent Nemesis neocracy Neolithic neology neophyte nestle nestling nettle network neural neurology neuter neutral Newtonian niggardly nihilist nil nimble nit nocturnal noiseless noisome noisy nomad nomic nominal nominate nominee nonentity nonpareil norm normalcy Norman nostrum notorious novice nowadays nowhere noxious nuance nucleus nude nugatory nuisance numerical nunnery nuptial nurture nutriment nutritive oaken oakum obdurate obelisk obese obesity obituary objective objector obligate oblique oblivion oblong obnoxious obsequies observant obsolete obstinacy obstruct obtrude obtrusive obvert obviate occasion Occident occlude occult occupant octagon octave octavo ocular oculist oddity ode odious odium odorous off offhand officiate officious offshoot ogre ointment olfactory ominous omission onerous onrush onset onslaught onus opaque operate operative operator operetta opinion opponent opportune opposite optic optician optics optimism option optometry opulence opulent oral orate oration orator oratorio oratory ordeal ordinal ordnance orgies origin original originate ornate orthodox orthodoxy oscillate osculate ossify ostracism ostracize ought oust outbreak outburst outcast outcry outdo outlast outlaw outlive outpost outrage outreach outride outrigger outright outskirt outstrip outweigh overdo overdose overeat overhang overleap overlord overpass overpay overpower overreach overrun oversee overseer overthrow overtone overture pacify packet pact pagan pageant palate palatial palette palinode pall palliate pallid palpable palsy paly pamphlet panacea pandemic panegyric panel panic panoply panorama pantheism Pantheon pantomime papacy papyrus parable paradox paragon parallel paralysis paralyze paramount paramour pare parentage Pariah parish Parisian parity parlance parley parlor parody paroxysm parricide parse partible partition partisan passible passive pastoral paternal paternity pathos patriarch patrician patrimony patronize patter paucity pauper pauperism pavilion payee peaceable peaceful peccable peccant pectoral pecuniary pedagogue pedagogy pedal pedant peddle pedestal pedigree peddler peerage peerless peevish pellucid penalty penance penchant pendant pendulous pendulum penetrate penitence pennant pension pentagram pentad pentagon penurious penury perceive percolate perennial perfidy perforate perform perfumery perhaps perigee perjure perjury permanent permeate persevere persist personage personal personnel perspire persuade pertinent perturb perusal pervade pervasion pervasive perverse pervert pervious pestilent peter petrify petulance petulant pharmacy philander philately philology phonetic phonic phonogram phonology physicist physics physique picayune piccolo piece piecemeal pillage pillory pincers pinchers pinnacle pioneer pious pique piteous pitiable pitiful pitiless pittance placate placid platitude plaudit plausible playful plea pleasant plebeian pledgee pledgeor plenary plenitude plenteous plumb plummet plural plurality pneumatic poesy poetaster poetic poetics poignancy poignant poise polar polemics pollen pollute polyarchy polycracy polygamy polyglot polygon pommel pomposity pompous ponder ponderous pontiff populace populous portend portent portfolio posit position positive posse possess possessor possible postdate posterior potency potent potentate potential potion powerless prate prattle preamble precede precedent precipice precise precision preclude precursor predatory predicate predict preempt preengage preexist preface prefatory prefer prefix prejudice prelacy prelate prelude premature premier premise preoccupy preordain presage prescient prescript pretext prevalent prickle priggish prim prima primer primeval primitive principal principle priory pristine privateer privilege privity privy probate probation probe probity procedure proceed proctor prodigal prodigy professor proffer profile profiteer profuse progeny prolific prolix prologue prolong promenade prominent promoter propagate propel propeller prophecy prophesy propriety prosaic proscribe proselyte prosody prostrate protector protege protocol prototype protract protrude proverb provident proviso prowess proxy prudence prudery prurient pseudonym psychic pudgy puerile puissant pulmonary punctual pungent pungency punitive pupilage purgatory purl purloin purport purveyor pyre pyromania pyx quackery quadrate quadruple qualify qualm quandary quantity quarter quarterly quartet quarto quay querulous query queue quibble quiescent quiet quietus quintet quite Quixotic rabid racy radiance radiate radical radix raillery ramify ramose rampant rampart rancor rankle rapacious rapid rapine rapt raptorial ration raucous ravage ravenous ravine reaction readily readjust ready realism rearrange reassure rebuff rebuild rebut recant recapture recede receptive recessive reck reckless reclaim recline recluse reclusory recognize recoil recollect recourse recover recreant recreate recruit rectify rectitude recur recure recurrent redolent redolence redound redress reducible redundant refer referrer referable referee refinery reflector reform reformer refract refusal refute regale regalia regality regent regicide regime regimen regiment regnant regress regretful reign reimburse rein reinstate reiterate rejoin relapse relegate relent relevant reliance reliant reliquary relish reluctant remiss remission remodel rendition renovate reparable repartee repeal repel repellent repertory repine replenish replete replica reprehend repress reprieve reprimand reprisal reprobate reproduce reproof repudiate repugnant repulse repulsive repute requiem requisite requital requite rescind reseat resent reservoir residue resilient resistant resistive resonance resonance resonate resource respite resurgent retaliate retch retention reticence reticent retinue retort retouch retrace retract retrench retrieve reunite revere reverent reversion revert revile revisal revise revoke rhapsody rhetoric ribald riddance ridicule rife rightful rigmarole rigor rigorous ripplet risible rivulet robust rondo rookery rotary rotate rote rotund rue ruffian ruminant ruminate rupture rustic ruth sacrifice sacrilege safeguard sagacious salacious salience salient saline salutary salvage salvo sanction sanctity sanguine sapid sapience sapient sarcasm sardonic satiate satire satiric satirize satyr savage savor scabbard scarcity scholarly scintilla scope scoundrel scribble scribe script scruple scuttle scythe seance sear sebaceous secant secede secession seclude seclusion secondary secondly secrecy secretary secretive sedate sedentary sediment sedition seditious seduce sedulous seer seethe seignior seize selective semblance seminar seminary senile sensation sense sensitive sensorium sensual sensuous sentence sentience sentient sentinel separable separate sepulcher sequel sequence sequent sequester sergeant service servitude severance severely sextet sextuple sheer shiftless shrewd shriek shrinkage shrivel shuffle sibilance sibilant sibilate sidelong sidereal siege similar simile simplify simulate sinecure singe sinister sinuosity sinuous sinus siren sirocco skeptic skiff skirmish sleight slight slothful sluggard sociable socialism socialist sociology Sol solace solar solder soldier solecism solicitor soliloquy solstice soluble solvent somber somnolent sonata sonnet sonorous sophism sophistry soprano sorcery sordid souvenir sparse Spartan spasmodic specialty specie species specimen specious spectator specter spectrum speculate spheroid spinous spinster sprightly spurious squabble squalid squatter stagnant stagnate stagy staid stallion stanchion stanza static statics statuette stature statute stealth stellar steppe sterling stifle stigma stiletto stimulant stimulate stimulus stingy stipend Stoicism stolid strait stratagem stratum streamlet stringent stripling studious stultify stupor suasion suave subacid subjacent subjugate submarine submerge submittal subside subsist subtend subtle subvert succeed success successor succinct succulent succumb suffrage suffuse summary sumptuous superadd superb superheat supersede supine supplant supple suppress surcharge surety surfeit surmise surmount surrogate surround surveyor suspense swarthy Sybarite sycophant syllabic syllable syllabus sylph symmetry symphonic symphony syndicate syneresis synod synonym synopsis tableau tacit taciturn tack tact tactician tactics tangency tangent tangible tannery tantalize tapestry tarnish taut taxation taxidermy technic technique teem telepathy telephony telescope telltale temerity temporal temporary temporize tempt tempter tenacious tenant tendency tenet tenor tense tentative tenure termagant terminal terminate terminus terrify terse testament testator thearchy theism theocracy theocrasy theology theorist theorize therefor thermal thesis thrall tilth timbre timorous tincture tinge tipsy tirade tireless tiresome Titanic toilsome tolerable tolerance tolerant tolerate torpor torrid tortious tortuous torturous tractable trait trammel tranquil transact transcend transfer transfuse transient translate transmit transmute transpire travail travesty treachery treatise treble trebly tremor tremulous trenchant trestle triad tribune trickery tricolor tricycle trident triennial trimness trinity trio triple tripod trisect trite triumvir trivial truculent truism truthful turgid turpitude tutelage tutelar tutorship twinge typical typify tyranny tyro ulterior ultimate ultimatum umbrage unanimous unanimity unbelief unbiased unbridled uncommon unction unctuous undeceive underman undersell underlie underling undermine underrate undue undulate undulous ungainly unguent unify unique unison unisonant Unitarian unlawful unlimited unnatural unsettle untimely untoward unwieldy unwise unyoke upbraid upcast upheaval upheave uppermost uproot upturn urban urbanity urchin urgency usage usurious usurp usury utility utmost vacate vaccinate vacillate vacuous vacuum vagabond vagrant vainglory vale valid valorous vapid vaporizer variable variance variant variation variegate vassal vegetal vegetate vehement velocity velvety venal vendible vendition vendor veneer venerable venerate venereal venial venison venom venous veracious veracity verbatim verbiage verbose verdant verify verily verity vermin vernal versatile version vertex vertical vertigo vestige vestment veto vicarious viceroy vie vigilance vigilant vignette vincible vindicate vinery viol viola violator violation virago virile virtu virtual virtuoso virulence virulent visage viscount vista visual visualize vitality vitalize vitiate vivacity vivify vocable vocative vogue volant volatile volition volitive voluble voracious vortex votary votive vulgarity waif waistcoat waive wampum wane warlike wavelet weal wean wearisome wee whereupon wherever wherewith whet whimsical whine wholly wield wile winsome wintry wiry witless witling witticism wittingly wizen wrangle wreak wrest writhe writing wry yearling zealot zeitgeist zenith zephyr zodiac";
    wordArray = s.split(" ");
    wordCount = wordArray.length;
  }


  private void gameOver(boolean win){
        // create an intent to change to GameOverActivity
    Intent I = new Intent(this, GameOverActivity.class);

    // add points and win-lose to the intent
    if(!win){okGuessCount = 0;}
    I.putExtra("win", win);
    I.putExtra("points",okGuessCount);

    // send intent and clear the game board
    startActivity(I);
    clearGamePage();

  }


  private void callGameOver(int delay, final boolean win){
    // show any remaining text
    completeText();

    // convert delay to millisec
    int ms = delay * 1000;

    // create a runnable object that calls gameOver
    Runnable wait3sec = new Runnable(){
      public void run(){gameOver(win);}
    };

    // create handler that will call the run method
    // after the delay
    Handler H = new Handler();
    H.postDelayed(wait3sec,ms);
  }


  private void completeText(){
    String t = "";

    // reference the LinearLayout that contains the text
    LinearLayout LL = (LinearLayout)findViewById(R.id.gameact);
    int redText = getResources().getColor(R.color.autumnText);

    // fill each letter of the word if not filled already
    for(int i = 0; i < wordLen; i++){

      // get the TextView and Text at letter position i
      TextView TV = (TextView)LL.getChildAt(i);
      t = TV.getText().toString();

      // if the text is not set, set it
      if(t.compareTo("_") == 0){
        TV.setText(Word.substring(i, i + 1));
        TV.setTextColor(redText);
      }
    }
  }  // end fcn completeText


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    Intent I = null;

    //noinspection SimplifiableIfStatement
    if (id == R.id.menu_single) {I = new Intent(this, GameActivity.class);}
    else if(id == R.id.menu_two){I = new Intent(this, EnterWordActivity.class);}
    else if(id == R.id.menu_sms){I = new Intent(this, GetSmsActivity.class);}
      else if(id == R.id.menu_scores){I = new Intent(this, ScoresActivity.class);}
        else if(id == R.id.menu_exit){System.exit(0);}

    // goto next sreen
    startActivity(I);
    return super.onOptionsItemSelected(item);
  }
}
