/*
 * (c) 2016 by Peter Stamfest <peter@stamfest.at>
 * 
 * All rights reserved. Redistribution in any form (source or
 * compiled) prohibited.
 */
package net.stamfest.randomtests.nist.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import junit.framework.Assert;
import net.stamfest.randomtests.nist.utils.AperiodicTemplate;
import org.junit.Test;

/**
 *
 * @author Peter Stamfest
 */
public class AperiodicTemplateTest {
    @Test
    public void len02() throws NoSuchAlgorithmException {
        Assert.assertEquals("19d8e8cf6b93224d3388548d5f8bdee4cd4e033d416d8631b8c44db208da788d", sha256ForTemplateLength(2));
    }
    
    @Test
    public void len03() throws NoSuchAlgorithmException {
        Assert.assertEquals("9aa4d6faa2410c11c1920edbcfe0b4ee11149412b861b62b2cc9dc564e3ed625", sha256ForTemplateLength(3));
    }
    
    @Test
    public void len04() throws NoSuchAlgorithmException {
        Assert.assertEquals("df5399cb519eaf20798b21568f80d144e7f2bf3bdb05666f08815af26dd30eda", sha256ForTemplateLength(4));
    }
    
    @Test
    public void len05() throws NoSuchAlgorithmException {
        Assert.assertEquals("30d80082479938a99bda5b7e517cb0c5f79929b55fbdc963a72ef8c9c221a510", sha256ForTemplateLength(5));
    }
    
    @Test
    public void len06() throws NoSuchAlgorithmException {
        Assert.assertEquals("43b5ff050601d2678ec52c9748cbd3170b8be2c17fc41a55d94bcf384fca4c35", sha256ForTemplateLength(6));
    }
    
    @Test
    public void len07() throws NoSuchAlgorithmException {
        Assert.assertEquals("df1d6986f4826e5790e9219ba7190461529f8c601bc45fdc0c5f4207c01732f3", sha256ForTemplateLength(7));
    }
    
    @Test
    public void len08() throws NoSuchAlgorithmException {
        Assert.assertEquals("3b9ad80eefd9a9517a8e9b6544ba50a5233f0dfd07af540e8c8e09fe1c78c96d", sha256ForTemplateLength(8));
    }
    
    @Test
    public void len09() throws NoSuchAlgorithmException {
        Assert.assertEquals("8a70fa8ca2368a048a3261a73a7c57760b522370e7a8b5a52891c91acf0f1dca", sha256ForTemplateLength(9));
    }
    
    @Test
    public void len10() throws NoSuchAlgorithmException {
        Assert.assertEquals("9470fe8a6c22dcf264150c89a9c45df84a13cebc39795199441b7d56f1739663", sha256ForTemplateLength(10));
    }
    
    @Test
    public void len11() throws NoSuchAlgorithmException {
        Assert.assertEquals("881ae35e37443e7a77f84130b6a1a585d93a4361daab7ee397c0b245e3425e9d", sha256ForTemplateLength(11));
    }
    
    @Test
    public void len12() throws NoSuchAlgorithmException {
        Assert.assertEquals("96fb9b8436f4077b08cf4ef443156a6ad1d580dc429d5d070617e697c1162304", sha256ForTemplateLength(12));
    }
    
    @Test
    public void len13() throws NoSuchAlgorithmException {
        Assert.assertEquals("858698414d9a648c5b73b04e48103c291ef2d5897c56d71ce619309b81ed9866", sha256ForTemplateLength(13));
    }
    
    @Test
    public void len14() throws NoSuchAlgorithmException {
        Assert.assertEquals("1099e9ca20b05b818329fc36ebf0161dd5ab99fa284abb9ad0b0fa35c948789d", sha256ForTemplateLength(14));
    }
    
    @Test
    public void len15() throws NoSuchAlgorithmException {
        Assert.assertEquals("b64a1eb81109edf53ac4618cb7d58253de4bce40ee33615e3d795cedfb4ba195", sha256ForTemplateLength(15));
    }
    
    @Test
    public void len16() throws NoSuchAlgorithmException {
        Assert.assertEquals("fda22503e54af6875c1b15a9e71dcfe8df601449d337ebbbe84eb54a911328b0", sha256ForTemplateLength(16));
    }
    
    @Test
    public void len17() throws NoSuchAlgorithmException {
        Assert.assertEquals("346d7535f3761216550c8d599f9e55d1ee5dd02a381a1fc565090b8d95dcc89b", sha256ForTemplateLength(17));
    }
    
    @Test
    public void len18() throws NoSuchAlgorithmException {
        Assert.assertEquals("6f562ce0248e56df1dafbe41abfb32d3a21bf2ae3dc87effac903d023abe051f", sha256ForTemplateLength(18));
    }
    
    @Test
    public void len19() throws NoSuchAlgorithmException {
        Assert.assertEquals("6a9fca40ed4d15a26ba3e0f3c9ee0fce507739cf8b292af0e6acedc752445b38", sha256ForTemplateLength(19));
    }
    
    @Test
    public void len20() throws NoSuchAlgorithmException {
        Assert.assertEquals("d3719ca2c947fd62002547d2efb225aee7280692638a106463a3f786232302ef", sha256ForTemplateLength(20));
    }
    
    @Test
    public void len21() throws NoSuchAlgorithmException {
        Assert.assertEquals("119596d19e7323855db5011642617f78744a0a123cfbb53054f56ab6d4fdeb8e", sha256ForTemplateLength(21));
    }
    
    public void numX(int x, int count) {
        Assert.assertEquals(count, new AperiodicTemplate(x).getCount());
    }
    
    public void cntX(int x) {
        AperiodicTemplate at = new AperiodicTemplate(x);
        int count = 0;
        for (Long l : at) {
            count++;
        }
        Assert.assertEquals(at.getCount(), count);
    }

    
    @Test    
    public void num2() {
        numX(2, 2);
    }
    
    @Test    
    public void num3() {
        numX(3, 4);
    }

    @Test
    public void num4() {
        numX(4, 6);
    }

    
    @Test
    public void num5() {
        numX(5, 12);
    }

    
    @Test
    public void num6() {
        numX(6, 20);
    }

    
    @Test
    public void num7() {
        numX(7, 40);
    }

    
    @Test
    public void num8() {
        numX(8, 74);
    }

    
    @Test
    public void num9() {
        numX(9, 148);
    }

    
    @Test
    public void num10() {
        numX(10, 284);
    }

    
    @Test
    public void num11() {
        numX(11, 568);
    }

    
    @Test
    public void num12() {
        numX(12, 1116);
    }

    
    @Test
    public void num13() {
        numX(13, 2232);
    }

    
    @Test
    public void num14() {
        numX(14, 4424);
    }

    
    @Test
    public void num15() {
        numX(15, 8848);
    }

    @Test
    public void cnt15() {
        cntX(15);
    }

    @Test
    public void manyCnt() {
        for(int i = 2 ; i < 25 ; i++) {
            cntX(i);
        }
    }


    /*
    
19d8e8cf6b93224d3388548d5f8bdee4cd4e033d416d8631b8c44db208da788d  templates/template2
9aa4d6faa2410c11c1920edbcfe0b4ee11149412b861b62b2cc9dc564e3ed625  templates/template3
df5399cb519eaf20798b21568f80d144e7f2bf3bdb05666f08815af26dd30eda  templates/template4
30d80082479938a99bda5b7e517cb0c5f79929b55fbdc963a72ef8c9c221a510  templates/template5
43b5ff050601d2678ec52c9748cbd3170b8be2c17fc41a55d94bcf384fca4c35  templates/template6
df1d6986f4826e5790e9219ba7190461529f8c601bc45fdc0c5f4207c01732f3  templates/template7
3b9ad80eefd9a9517a8e9b6544ba50a5233f0dfd07af540e8c8e09fe1c78c96d  templates/template8
8a70fa8ca2368a048a3261a73a7c57760b522370e7a8b5a52891c91acf0f1dca  templates/template9
9470fe8a6c22dcf264150c89a9c45df84a13cebc39795199441b7d56f1739663  templates/template10
881ae35e37443e7a77f84130b6a1a585d93a4361daab7ee397c0b245e3425e9d  templates/template11
96fb9b8436f4077b08cf4ef443156a6ad1d580dc429d5d070617e697c1162304  templates/template12
858698414d9a648c5b73b04e48103c291ef2d5897c56d71ce619309b81ed9866  templates/template13
1099e9ca20b05b818329fc36ebf0161dd5ab99fa284abb9ad0b0fa35c948789d  templates/template14
b64a1eb81109edf53ac4618cb7d58253de4bce40ee33615e3d795cedfb4ba195  templates/template15
fda22503e54af6875c1b15a9e71dcfe8df601449d337ebbbe84eb54a911328b0  templates/template16
346d7535f3761216550c8d599f9e55d1ee5dd02a381a1fc565090b8d95dcc89b  templates/template17
6f562ce0248e56df1dafbe41abfb32d3a21bf2ae3dc87effac903d023abe051f  templates/template18
6a9fca40ed4d15a26ba3e0f3c9ee0fce507739cf8b292af0e6acedc752445b38  templates/template19
d3719ca2c947fd62002547d2efb225aee7280692638a106463a3f786232302ef  templates/template20
119596d19e7323855db5011642617f78744a0a123cfbb53054f56ab6d4fdeb8e  templates/template21

    
    */

    String sha256ForTemplateLength(int len) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
        AperiodicTemplate at = new AperiodicTemplate(len);
        byte[] nl = "\n".getBytes();
        for (Long l : at) {
            sha1.update(at.bitstring(l, " ").getBytes());
            sha1.update(nl);
        }
        byte[] digest = sha1.digest();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0 ; i < digest.length ; i++) {
            sb.append(String.format("%02x", digest[i]));
        }
        return sb.toString();
    }
    
    @Test 
    public void lengths() {
        
    }
}
