/**
 * Copyright (c) 2016-2017 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.farm.reactive;

import com.zerocracy.farm.sync.SyncProject;
import com.zerocracy.jstk.Project;
import com.zerocracy.jstk.fake.FkProject;
import com.zerocracy.pm.ClaimOut;
import com.zerocracy.pm.Claims;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link RvProject}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.11
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class RvProjectTest {

    @Test
    public void closesClaims() throws Exception {
        final AtomicBoolean done = new AtomicBoolean();
        final Project raw = new FkProject();
        final Flush flush = new Flush(
            raw,
            new Brigade(
                Collections.singletonList(
                    (project, xml) -> done.set(true)
                )
            )
        );
        final RvProject project = new RvProject(new SyncProject(raw), flush);
        final Claims claims = new Claims(project).bootstrap();
        claims.add(new ClaimOut().type("hello").token("test;tt"));
        while (true) {
            if (!claims.iterate().iterator().hasNext()) {
                break;
            }
        }
        MatcherAssert.assertThat(done.get(), Matchers.is(true));
    }

}
