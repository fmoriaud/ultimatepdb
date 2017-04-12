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
package mystructure;

import java.nio.file.attribute.FileTime;


public interface MyStructureIfc {

    FileTime getLastModificationTime();

    void setLastModificationTime(FileTime lastModificationTime);

    MyChainIfc getAminoChain(int i);

    MyChainIfc getNucleosideChain(int i);

    MyChainIfc getHetatmChain(int i);

    void setAminoChain(char[] chainId, MyChainIfc myNewChain);

    MyChainIfc[] getAllAminochains();

    MyChainIfc[] getAllHetatmchains();

    MyChainIfc[] getAllNucleosidechains();

    MyChainIfc getAminoMyChain(char[] chainId);

    MyChainIfc getNucleosideChain(char[] chainId);

    MyChainIfc getHeteroChain(char[] chainId);

    MyChainIfc[] getAllChainsRelevantForShapeBuilding();

    /**
     * Get All MyChains in the following order 	AMINOACID, HETATM, NUCLEOTIDE
     *
     * @return
     */
    MyChainIfc[] getAllChains();

    int getAminoMonomercount();

    int getAminoChainCount();

    int indexOfAnAminoChain(MyChainIfc myChain);

    char[] getFourLetterCode();

    void setFourLetterCode(char[] fourLetterCode);

    HBondDefinedByAtomAndMonomer[] getHbonds();

    void setHbonds(HBondDefinedByAtomAndMonomer[] hbonds);

    PairOfMyAtomWithMyMonomerAndMychainReferences[] getDisulfideBridges();

    void setDisulfideBridges(PairOfMyAtomWithMyMonomerAndMychainReferences[] disulfideBridges);

    String toV3000();

    void removeChain(char[] chainId);

    ExpTechniquesEnum getExpTechnique();
    String getPdbFileHash();
}
