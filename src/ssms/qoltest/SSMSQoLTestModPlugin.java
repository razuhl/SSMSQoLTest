/*
 * The MIT License
 *
 * Copyright 2020 Malte Schulze.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ssms.qoltest;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.characters.MarketConditionSpecAPI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.log4j.Level;
import org.lwjgl.util.vector.Vector2f;
import ssms.qol.properties.PropertiesContainer;
import ssms.qol.properties.PropertiesContainerConfiguration;
import ssms.qol.properties.PropertiesContainerConfigurationFactory;
import ssms.qol.properties.PropertiesContainerMerger;
import ssms.qol.properties.PropertyConfigurationBoolean;
import ssms.qol.properties.PropertyConfigurationContainer;
import ssms.qol.properties.PropertyConfigurationFloat;
import ssms.qol.properties.PropertyConfigurationInteger;
import ssms.qol.properties.PropertyConfigurationListContainer;
import ssms.qol.properties.PropertyConfigurationListPrimitive;
import ssms.qol.properties.PropertyConfigurationListSelectable;
import ssms.qol.properties.PropertyConfigurationSelectable;
import ssms.qol.properties.PropertyConfigurationString;
import ssms.qol.properties.PropertyValueGetter;
import ssms.qol.properties.PropertyValueSetter;

/**
 *
 * @author Malte Schulze
 */
public class SSMSQoLTestModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() throws Exception {
        //These are settings that exist without any dependancy to a running playthorugh. E.g. content that would otherwise be in configuration files.
        configure();
    }
    
    @Override
    public void onGameLoad(boolean newGame) {
        //These are settings that reference values from a playthrough. E.g. starsystems, planets or the players fleet.
        configureSettingsGame();
    }

    static protected enum Option {
        option1, option2, option3, option4, option5, option6, option7, option8, option9, option10;
    }
    static protected class TestSettings {
        public Integer i;
        public Float f;
        public String s;
        public Boolean b;
        public Option o;
        public Vector2f complex;
        public List<Integer> li;
        public List<Float> lf;
        public List<String> ls;
        public List<Boolean> lb;
        public List<Option> lo;
        public List<Vector2f> lcomplex;

        public TestSettings() {
        }

        public TestSettings(Integer i, Float f, String s, Boolean b, Option o, Vector2f complex, List<Integer> li, List<Float> lf, List<String> ls, List<Boolean> lb, List<Option> lo, List<Vector2f> lcomplex) {
            this.i = i;
            this.f = f;
            this.s = s;
            this.b = b;
            this.o = o;
            this.complex = complex;
            this.li = li;
            this.lf = lf;
            this.ls = ls;
            this.lb = lb;
            this.lo = lo;
            this.lcomplex = lcomplex;
        }
        
    }
    static protected Map<String,TestSettings> settings;
    
    static public void configure() {
        settings = new HashMap<>();
        settings.put("0", new TestSettings());
        settings.put("1", new TestSettings(1,1f,"1",true,Option.option1,new Vector2f(1f, 2f),Arrays.asList(1),Arrays.asList(1f),Arrays.asList("1"),Arrays.asList(Boolean.TRUE),
                Arrays.asList(Option.option1),Arrays.asList(new Vector2f(3f, 4f))));
        settings.put("2", new TestSettings(2,2f,"2",false,Option.option2,new Vector2f(),Arrays.asList(1,2),Arrays.asList(1f,2f),Arrays.asList("1","2"),
                Arrays.asList(Boolean.TRUE,Boolean.FALSE),Arrays.asList(Option.option1,Option.option2),Arrays.asList(new Vector2f(1f,2f),new Vector2f(3f,4f))));
        
        PropertiesContainerConfigurationFactory confFactory = PropertiesContainerConfigurationFactory.getInstance();
        PropertiesContainerConfiguration<Vector2f> confCoordinates = confFactory.getOrCreatePropertiesContainerConfiguration("SSMSQoLTestSettingsCoordinates", Vector2f.class);
        confCoordinates.addProperty(new PropertyConfigurationFloat<>("x","X","X-Coordinate must be greater or equal to 0 and cannot be null.",0f,10,new PropertyValueGetter<Vector2f, Float>() {
            @Override
            public Float get(Vector2f sourceObject) {
                return sourceObject.x;
            }
        }, new PropertyValueSetter<Vector2f, Float>() {
            @Override
            public void set(Vector2f sourceObject, Float value) {
                sourceObject.x = value;
            }
        }, false, 0f, Float.MAX_VALUE));
        confCoordinates.addProperty(new PropertyConfigurationFloat<>("y","Y","Y-Coordinate must be greater or equal to 0 and cannot be null.",0f,20,new PropertyValueGetter<Vector2f, Float>() {
            @Override
            public Float get(Vector2f sourceObject) {
                return sourceObject.y;
            }
        }, new PropertyValueSetter<Vector2f, Float>() {
            @Override
            public void set(Vector2f sourceObject, Float value) {
                sourceObject.y = value;
            }
        }, false, 0f, Float.MAX_VALUE));
        confCoordinates.configureMinorApplicationScoped(new PropertyValueGetter<PropertiesContainer<Vector2f>, String>() {
            @Override
            public String get(PropertiesContainer<Vector2f> con) {
                return new StringBuilder("(").append(con.getFieldValue("x", Float.class)).append(",").append(con.getFieldValue("y", Float.class)).append(")").toString();
            }
        });
        
        PropertiesContainerConfiguration<TestSettings> conf = confFactory.getOrCreatePropertiesContainerConfiguration("SSMSQoLTestSettings", TestSettings.class);
        conf.addProperty(new PropertyConfigurationBoolean<>("b","Boolean","A true, false or nothing setting.",null,10, new PropertyValueGetter<TestSettings, Boolean>() {
            @Override
            public Boolean get(TestSettings sourceObject) {
                return sourceObject.b;
            }
        }, new PropertyValueSetter<TestSettings, Boolean>() {
            @Override
            public void set(TestSettings sourceObject, Boolean value) {
                sourceObject.b = value;
            }
        }, true));
        conf.addProperty(new PropertyConfigurationInteger<>("i","Integer","A whole number ranging from -10 to 10.",null,20, new PropertyValueGetter<TestSettings, Integer>() {
            @Override
            public Integer get(TestSettings sourceObject) {
                return sourceObject.i;
            }
        }, new PropertyValueSetter<TestSettings, Integer>() {
            @Override
            public void set(TestSettings sourceObject, Integer value) {
                sourceObject.i = value;
            }
        }, true,-10,10));
        conf.addProperty(new PropertyConfigurationFloat<>("f","Float","A decimal number ranging from -10.0 to 10.0 including null.",null,30, new PropertyValueGetter<TestSettings, Float>() {
            @Override
            public Float get(TestSettings sourceObject) {
                return sourceObject.f;
            }
        }, new PropertyValueSetter<TestSettings, Float>() {
            @Override
            public void set(TestSettings sourceObject, Float value) {
                sourceObject.f = value;
            }
        },true,-10f,10f));
        conf.addProperty(new PropertyConfigurationString<>("s","String","A sequence of characters.",null,40, new PropertyValueGetter<TestSettings, String>() {
            @Override
            public String get(TestSettings sourceObject) {
                return sourceObject.s;
            }
        }, new PropertyValueSetter<TestSettings, String>() {
            @Override
            public void set(TestSettings sourceObject, String value) {
                sourceObject.s = value;
            }
        }, true));
        conf.addProperty(new PropertyConfigurationSelectable<TestSettings,String>("o","Option","A selectable option.",null,45,String.class,new PropertyValueGetter<TestSettings, String>() {
            @Override
            public String get(TestSettings sourceObject) {
                return sourceObject.o == null ? null : sourceObject.o.name();
            }
        }, new PropertyValueSetter<TestSettings, String>() {
            @Override
            public void set(TestSettings sourceObject, String value) {
                sourceObject.o = value == null ? null : Option.valueOf(value);
            }
        }, true) {
            @Override
            public List<String> buildOptions() {
                List<String> names = new ArrayList<>();
                for ( Option o : Option.values() ) names.add(o.name());
                return names;
            }

            @Override
            public String getOptionLabel(String o) {
                return o;
            }
        });
        conf.addProperty(new PropertyConfigurationContainer<>("complex","Complex","A complex object with its own properties.",null,"SSMSQoLTestSettingsCoordinates",Vector2f.class,47,new PropertyValueGetter<TestSettings, Vector2f>() {
            @Override
            public Vector2f get(TestSettings sourceObject) {
                return sourceObject.complex;
            }
        }, new PropertyValueSetter<TestSettings, PropertiesContainer>() {
            @Override
            public void set(TestSettings sourceObject, PropertiesContainer value) {
                if ( value == null ) sourceObject.complex = null;
                else {
                    sourceObject.complex = new Vector2f((Float)value.getFieldValue("x", Float.class),(Float)value.getFieldValue("y", Float.class));
                }
            }
        }, true));
        conf.addProperty(new PropertyConfigurationListPrimitive<>("li","List of Integers","A list of integers that allows adding and removing.",null,50,new PropertyValueGetter<TestSettings, List>() {
            @Override
            public List get(TestSettings sourceObject) {
                return sourceObject.li;
            }
        }, new PropertyValueSetter<TestSettings, List>() {
            @Override
            public void set(TestSettings sourceObject, List value) {
                sourceObject.li = value;
            }
        }, true,Integer.class,true,true,true,new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return null;
            }
        }));
        conf.addProperty(new PropertyConfigurationListPrimitive<>("lf","List of Floats","A list of decimals that allows adding and removing.",null,60,new PropertyValueGetter<TestSettings, List>() {
            @Override
            public List get(TestSettings sourceObject) {
                return sourceObject.lf;
            }
        }, new PropertyValueSetter<TestSettings, List>() {
            @Override
            public void set(TestSettings sourceObject, List value) {
                sourceObject.lf = value;
            }
        }, true,Float.class,true,true,true,new Callable<Float>() {
            @Override
            public Float call() throws Exception {
                return null;
            }
        }));
        conf.addProperty(new PropertyConfigurationListPrimitive<>("ls","List of Strings","A list of character sequences that allows adding and removing.",null,70,new PropertyValueGetter<TestSettings, List>() {
            @Override
            public List get(TestSettings sourceObject) {
                return sourceObject.ls;
            }
        }, new PropertyValueSetter<TestSettings, List>() {
            @Override
            public void set(TestSettings sourceObject, List value) {
                sourceObject.ls = value;
            }
        }, true,String.class,true,true,true,new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        }));
        conf.addProperty(new PropertyConfigurationListPrimitive<>("lb","List of Booleans","A list of true, false or nothing settings that allows adding and removing.",null,80,new PropertyValueGetter<TestSettings, List>() {
            @Override
            public List get(TestSettings sourceObject) {
                return sourceObject.lb;
            }
        }, new PropertyValueSetter<TestSettings, List>() {
            @Override
            public void set(TestSettings sourceObject, List value) {
                sourceObject.lb = value;
            }
        }, true,Boolean.class,true,true,true,new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return null;
            }
        }));
        conf.addProperty(new PropertyConfigurationListSelectable<TestSettings>("lo","List of Options","A sortable list of selectable options that allows adding and removing.",null,90,new PropertyValueGetter<TestSettings, List>() {
            @Override
            public List get(TestSettings sourceObject) {
                return sourceObject.lo;
            }
        }, new PropertyValueSetter<TestSettings, List>() {
            @Override
            public void set(TestSettings sourceObject, List value) {
                sourceObject.lo = value;
            }
        }, true, Option.class) {
            @Override
            public List buildOptions() {
                return Arrays.asList(Option.values());
            }

            @Override
            public String getOptionLabel(Object o) {
                return ((Option)o).name();
            }
        });
        conf.addProperty(new PropertyConfigurationListContainer<>("lcomplex","List of complex Objects","A list of complex object which each have their own properties.",null,100,
            new PropertyValueGetter<TestSettings, List>() {
                @Override
                public List get(TestSettings sourceObject) {
                    return sourceObject.lcomplex;
                }
            },new PropertyValueSetter<TestSettings, List>() {
                @Override
                public void set(TestSettings sourceObject, List value) {
                    sourceObject.lcomplex = value;
                }
            }, true,"SSMSQoLTestSettingsCoordinates", true, true, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return new Vector2f();
            }
        }));
        
        //Filler properties to ensure the scroll pane on the container screen works
        for ( int i = 0; i < 20; i++ ) {
            conf.addProperty(new PropertyConfigurationInteger<>("filler"+i,"Filler "+i,"Pointless spam to test scroll functionality.",null,200+i, new PropertyValueGetter<TestSettings, Integer>() {
                @Override
                public Integer get(TestSettings sourceObject) {
                    return null;
                }
            }, new PropertyValueSetter<TestSettings, Integer>() {
                @Override
                public void set(TestSettings sourceObject, Integer value) {
                    
                }
            }, true,Integer.MIN_VALUE,Integer.MAX_VALUE));
        }
        
        conf.configureApplicationScopedMultipleInstances("SSMSQoLTestSettings", settings.values(), true, new PropertyValueGetter<TestSettings, String>() {
            @Override
            public String get(TestSettings sourceObject) {
                for ( Map.Entry<String,TestSettings> e : settings.entrySet() ) {
                    if ( e.getValue() == sourceObject ) {
                        return e.getKey();
                    }
                }
                return "undefined";
            }
        }, new PropertyValueGetter<String, TestSettings>() {
            @Override
            public TestSettings get(String sourceObjectId) {
                return settings.get(sourceObjectId);
            }
        }, new PropertyValueGetter<TestSettings, String>() {
            @Override
            public String get(TestSettings sourceObject) {
                for ( Map.Entry<String,TestSettings> e : settings.entrySet() ) {
                    if ( e.getValue() == sourceObject ) {
                        return e.getKey();
                    }
                }
                return "undefined";
            }
        }, new PropertyValueGetter<PropertiesContainer<TestSettings>, String>() {
            @Override
            public String get(PropertiesContainer<TestSettings> sourceObject) {
                return sourceObject == null ? "undefined" : sourceObject.getId();
            }
        });
    }
    
    protected void configureSettingsGame() {
        PropertiesContainerConfigurationFactory confFactory = PropertiesContainerConfigurationFactory.getInstance();
        PropertiesContainerConfiguration<PlanetAPI> confPlanet = confFactory.getOrCreatePropertiesContainerConfiguration("planet", PlanetAPI.class);
        confPlanet.addProperty(new PropertyConfigurationString<>("name","Name","tooltip","undefined",10, 
                new PropertyValueGetter<PlanetAPI,String>() {
                    @Override
                    public String get(PlanetAPI sourceObject) {
                        return sourceObject.getName();
                    }
                },  
                new PropertyValueSetter<PlanetAPI,String>() {
                    @Override
                    public void set(PlanetAPI sourceObject, String value) {
                        sourceObject.setName(value);
                    }
                }, false));
        confPlanet.addProperty(new PropertyConfigurationListSelectable<PlanetAPI>("conditions","Conditions","Which conditions are active on the planet.",
                new ArrayList<>(),
                20,new PropertyValueGetter<PlanetAPI, List>() {
            @Override
            public List get(PlanetAPI sourceObject) {
                if ( sourceObject.getMarket() != null ) {
                    ArrayList<MarketConditionSpecAPI> specs = new ArrayList<>(sourceObject.getMarket().getConditions().size());
                    for ( MarketConditionAPI con : sourceObject.getMarket().getConditions() ) specs.add(con.getSpec());
                    return specs;
                } else return null;
            }
        },null,false,MarketConditionSpecAPI.class) {
            @Override
            public List buildOptions() {
                return Global.getSettings().getAllMarketConditionSpecs();
            }

            @Override
            public String getOptionLabel(Object o) {
                return o != null ? ((MarketConditionSpecAPI)o).getName() : "";
            }
        });
        confPlanet.addSetter(new PropertiesContainerMerger<PlanetAPI>() {
            @Override
            public boolean merge(PropertiesContainer<PlanetAPI> container, PlanetAPI sourceObject) {
                MarketAPI market = sourceObject.getMarket();
                if ( market != null ) {
                    List<MarketConditionSpecAPI> conditionsConfigured = container.getFieldValue("conditions", List.class);
                    if ( conditionsConfigured != null ) {
                        List<MarketConditionAPI> conditions = market.getConditions();
                        for ( int i = conditions.size()-1; i >= 0; i-- ) {
                            if ( !conditionsConfigured.contains(conditions.get(i).getSpec()) ) {
                                market.removeCondition(conditions.get(i).getId());
                            }
                        }
                        for ( MarketConditionSpecAPI api : conditionsConfigured ) {
                            try {
                                if ( !market.hasCondition(api.getId()) ) {
                                    market.addCondition(api.getId());
                                    market.getCondition(api.getId()).setSurveyed(market.getSurveyLevel().equals(MarketAPI.SurveyLevel.FULL));
                                }
                            } catch (Throwable ex) {
                                Global.getLogger(SSMSQoLTestModPlugin.class).log(Level.ERROR, "Failed to apply condition "+api.getId()+"! Not all modded conditions can be applied so this might be sane.");
                                try {
                                    if ( market.hasCondition(api.getId()) ) {
                                        market.removeCondition(api.getId());
                                    }
                                } catch (Throwable t) {}
                            }
                        }
                        market.reapplyConditions();
                    }
                }
                return true;
            }
        });
        confPlanet.configureMinorGameScopedMultipleInstances(new PropertyValueGetter<PlanetAPI, String>() {
                @Override
                public String get(PlanetAPI sourceObject) {
                    return sourceObject.getId();
                }
            }, new PropertyValueGetter<String, PlanetAPI>() {
                @Override
                public PlanetAPI get(String sourceObject) {
                    for ( StarSystemAPI ssApi : Global.getSector().getStarSystems() ) {
                        for ( PlanetAPI pApi : ssApi.getPlanets() ) {
                            if ( pApi.getId().equals(sourceObject) ) return pApi;
                        }
                    }
                    return null;
                }
            }, new PropertyValueGetter<PlanetAPI, String>() {
                @Override
                public String get(PlanetAPI sourceObject) {
                    return sourceObject.getName();
                }
            }, new PropertyValueGetter<PropertiesContainer<PlanetAPI>, String>() {
                @Override
                public String get(PropertiesContainer<PlanetAPI> container) {
                    return container.getFieldValue("name", String.class);
                }
            });
        
        PropertiesContainerConfiguration<StarSystemAPI> confStarsystem = confFactory.getOrCreatePropertiesContainerConfiguration("starsystem", StarSystemAPI.class);
        confStarsystem.addProperty(new PropertyConfigurationString<>("name","Name","tooltip","undefined",10, 
                new PropertyValueGetter<StarSystemAPI,String>() {
                    @Override
                    public String get(StarSystemAPI sourceObject) {
                        return sourceObject.getName();
                    }
                },  
                new PropertyValueSetter<StarSystemAPI,String>() {
                    @Override
                    public void set(StarSystemAPI sourceObject, String value) {
                        sourceObject.setName(value);
                    }
                }, false));
        confStarsystem.addProperty(new PropertyConfigurationListContainer<>("planets","Planets","All planets inside the starsystem.",
                new ArrayList<>(),
                20,new PropertyValueGetter<StarSystemAPI, List>() {
            @Override
            public List get(StarSystemAPI sourceObject) {
                return sourceObject.getPlanets();
            }
                },null, false,"planet", false, false, null));
        confStarsystem.configureGameScopedMultipleInstances("Starsystems", Global.getSector().getStarSystems(), new PropertyValueGetter<StarSystemAPI, String>() {
                @Override
                public String get(StarSystemAPI sourceObject) {
                    return sourceObject.getId();
                }
            }, new PropertyValueGetter<String, StarSystemAPI>() {
                @Override
                public StarSystemAPI get(String sourceObject) {
                    for ( StarSystemAPI api : Global.getSector().getStarSystems() ) if ( api.getId().equals(sourceObject) ) return api;
                    return null;
                }
            }, new PropertyValueGetter<StarSystemAPI, String>() {
                @Override
                public String get(StarSystemAPI sourceObject) {
                    return sourceObject.getName();
                }
            }, new PropertyValueGetter<PropertiesContainer<StarSystemAPI>, String>() {
                @Override
                public String get(PropertiesContainer<StarSystemAPI> container) {
                    return container.getFieldValue("name", String.class);
                }
            });
    }
}
