/*
Author:
      Fabrice Moriaud <fmoriaud@ultimatepdb.org>

  Copyright (c) 2016 Fabrice Moriaud

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package pointWithProperties;

public enum StrikingProperties {
    DEHYDRON("Dehydron"),
    AROMATICRING("Aromatic Ring"),
    HYDROPHOBE("Hydrophobe"),
    POSITIVE_CHARGE("Positive Charge"),
    NEGATIVE_CHARGE("Negative Charge"),
    HBOND_DONNOR("HBond Donnor"),
    HBOND_ACCEPTOR("HBond Acceptor"),
    NONE("None");

    private String strikingPropertyName;

    StrikingProperties(String strikingPropertyName) {
        this.strikingPropertyName = strikingPropertyName;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append(strikingPropertyName);

        return result.toString();
    }


    public String getStrikingPropertyName() {
        return strikingPropertyName;
    }

}
