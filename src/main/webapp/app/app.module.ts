import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { JhipstercompanionSharedModule } from 'app/shared/shared.module';
import { JhipstercompanionCoreModule } from 'app/core/core.module';
import { JhipstercompanionAppRoutingModule } from './app-routing.module';
import { JhipstercompanionHomeModule } from './home/home.module';
import { JhipstercompanionEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { JhiMainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    JhipstercompanionSharedModule,
    JhipstercompanionCoreModule,
    JhipstercompanionHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    JhipstercompanionEntityModule,
    JhipstercompanionAppRoutingModule
  ],
  declarations: [JhiMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [JhiMainComponent]
})
export class JhipstercompanionAppModule {}
