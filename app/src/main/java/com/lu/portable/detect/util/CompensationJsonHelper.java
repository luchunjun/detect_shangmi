package com.lu.portable.detect.util;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class CompensationJsonHelper {
    private static HashMap <String, Integer> _compensation;
    private static CompensationJsonHelper _instance = null;

    static {
        _compensation = null;
    }

    private SpeedType speed;
    private TruckAxleType truck;

    public static CompensationJsonHelper create(Context context) {
        if (_instance != null) {
            return _instance;
        }
        try {
            InputStream inputStream = context.getAssets().open("config/compensationFactor.json");
            byte[] arrayOfByte = new byte[inputStream.available()];
            inputStream.read(arrayOfByte);
            inputStream.close();
            String configStr = new String(arrayOfByte);
            _instance = new Gson().fromJson(configStr, CompensationJsonHelper.class);
            return _instance;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap <String, Integer> getCompensation()
            throws NullPointerException {
        if (_instance == null) {
            throw new NullPointerException("CompensationJsonHelper is null");
        }
        if (_compensation == null) {
            _compensation = new HashMap();
            List <SpeedType.SpeedCompensation> speedCompensationList = _instance.speed.getItems();
            for (SpeedType.SpeedCompensation speedCompensation : speedCompensationList) {
                _compensation.put(speedCompensation.getKey(), Integer.valueOf(speedCompensation.getValue()));
            }
            List <TruckAxleType.TruckTypeCompensation> truckTypeCompensationList = _instance.truck.getTypes();
            for (TruckAxleType.TruckTypeCompensation truckTypeCompensation : truckTypeCompensationList) {
                for (TruckAxleType.TruckTypeCompensation.TypeFactor typeFactor : truckTypeCompensation.getItems()) {
                    _compensation.put(typeFactor.getKey(), Integer.valueOf(typeFactor.getTotal()));
                    for (CompensationJsonHelper.TruckAxleType.TruckTypeCompensation.Factor factor : typeFactor.getFactors()) {
                        _compensation.put(factor.getKey(), Integer.valueOf(factor.getValue()));
                    }
                }
            }
        }
        return _compensation;
    }

    public SpeedType getSpeed() {
        return _instance.speed;
    }

    public void setSpeed(SpeedType speedType) {
        _instance.speed = speedType;
    }

    public TruckAxleType getTruck() {
        return truck;
    }

    public void setTruck(TruckAxleType truckAxleType) {
        this.truck = truckAxleType;
    }

    public static class SpeedType {
        private List <SpeedCompensation> items;
        private String name;

        public List <SpeedCompensation> getItems() {
            return this.items;
        }

        public void setItems(List <SpeedCompensation> paramList) {
            this.items = paramList;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String paramString) {
            this.name = paramString;
        }

        public static class SpeedCompensation {
            private String key;
            private String name;
            private int value;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getValue() {
                return this.value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }
    }

    public static class TruckAxleType {
        private String name;
        private List <TruckTypeCompensation> types;

        public String getName() {
            return this.name;
        }

        public void setName(String paramString) {
            this.name = paramString;
        }

        public List <TruckTypeCompensation> getTypes() {
            return this.types;
        }

        public void setTypes(List <TruckTypeCompensation> paramList) {
            this.types = paramList;
        }

        public static class TruckTypeCompensation {
            private List <TypeFactor> items;
            private String name;

            public List <TypeFactor> getItems() {
                return this.items;
            }

            public void setItems(List <TypeFactor> paramList) {
                this.items = paramList;
            }

            public String getName() {
                return this.name;
            }

            public void setName(String paramString) {
                this.name = paramString;
            }

            public static class Factor {
                private String key;
                private String name;
                private int value;

                public String getKey() {
                    return this.key;
                }

                public void setKey(String paramString) {
                    this.key = paramString;
                }

                public String getName() {
                    return this.name;
                }

                public void setName(String paramString) {
                    this.name = paramString;
                }

                public int getValue() {
                    return this.value;
                }

                public void setValue(int paramInt) {
                    this.value = paramInt;
                }
            }

            public static class TypeFactor {
                private List <Factor> factors;
                private String key;
                private String name;
                private int total;

                public List <Factor> getFactors() {
                    return this.factors;
                }

                public void setFactors(List <Factor> paramList) {
                    this.factors = paramList;
                }

                public String getKey() {
                    return this.key;
                }

                public void setKey(String paramString) {
                    this.key = paramString;
                }

                public String getName() {
                    return this.name;
                }

                public void setName(String paramString) {
                    this.name = paramString;
                }

                public int getTotal() {
                    return this.total;
                }

                public void setTotal(int paramInt) {
                    this.total = paramInt;
                }
            }
        }
    }
}
