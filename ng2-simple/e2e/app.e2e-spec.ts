import { Ng2SimplePage } from './app.po';

describe('ng2-simple App', function() {
  let page: Ng2SimplePage;

  beforeEach(() => {
    page = new Ng2SimplePage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
