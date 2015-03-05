# MovieSearch
- Search movies via OMDb API
- Tested on Nexus 7, MovieSearch.apk under root category can be installed and run on Android devices

### UI structure design
- Use MovieActivity to host two fragments: MovieListFrament and MovieDetailFragment
- MovieActivity has diffent layouts for phones and tablets as require
- MovieListFrament contains a SearchView and a ListView
  - SearchView has OnQueryTextListener to moniter the input
  - ListView's adapter uses ViewHolder pattern for better performance
- MovieDetailFragment uses ImageView to display the movie poster

### Data structure design
- Basic data models: Movie and MovieDetail
- JSON parsers parse the JSON responses from OMDb
- The fragments should not communicate directly (e.g. when a movie is selected in the ListView). They should communicate via MovieActivity by using callbacks.
- All OMDb API calls (serach movie, retrieve movie detail and retrieve moive poster) should occur in the background (i.e. in a separate thread)
- Movie poster is loaded with scaled down image to avoid OutOfMemory error.

### Handling exceptions
- Search input has only one character
- No poster
- Invalid movie detail data
